package com.example.mountainlog.service;

import com.example.mountainlog.dto.ActivityStatsDto;
import com.example.mountainlog.dto.YearlyStatsDto;
import com.example.mountainlog.entity.Activity;
import com.example.mountainlog.entity.ActivityDetail;
import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    private User testUser;
    private Mountain testMountain;
    private Activity testActivity;
    private ActivityDetail testDetail;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");

        testMountain = new Mountain();
        testMountain.setMountainId(1);
        testMountain.setName("高尾山");

        testActivity = new Activity();
        testActivity.setActivityId(1);
        testActivity.setTitle("初めての登山");
        testActivity.setActivityDate(Date.valueOf(LocalDate.of(2023, 10, 1)));
        testActivity.setUser(testUser);
        testActivity.setMountain(testMountain);

        testDetail = new ActivityDetail();
        testDetail.setDistanceKm(5.5);
        testDetail.setDurationMinutes(120);
        testDetail.setElevationGain(400);
        testDetail.setMaxElevation(599);
    }

    @Test
    void testCreateActivity_Success() {
        // Arrange
        when(activityRepository.save(any(Activity.class))).thenAnswer(i -> {
            Activity saved = i.getArgument(0);
            saved.setActivityId(2);
            return saved;
        });

        // Act
        Activity result = activityService.createActivity(testActivity, testDetail, null);

        // Assert
        assertThat(result.getActivityId()).isEqualTo(2);
        assertThat(result.getActivityDetail()).usingRecursiveComparison().isEqualTo(testDetail);
        assertThat(testDetail.getActivity()).isEqualTo(result);

        verify(activityRepository, times(1)).save(testActivity);
    }

    @Test
    void testCreateActivity_NullArgs() {
        assertThrows(NullPointerException.class,
                () -> activityService.createActivity(null, new ActivityDetail(), null));
        assertThrows(NullPointerException.class,
                () -> activityService.createActivity(new Activity(), null, null));
    }

    @Test
    void testUpdateActivity_Existing() {
        // Arrange
        Activity updatedActivity = new Activity();
        updatedActivity.setTitle("更新タイトル");
        updatedActivity.setActivityDate(Date.valueOf(LocalDate.of(2023, 10, 2)));

        ActivityDetail updatedDetail = new ActivityDetail();
        updatedDetail.setDistanceKm(10.0);

        testActivity.setActivityDetail(testDetail);

        when(activityRepository.findById(1)).thenReturn(Optional.of(testActivity));
        when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

        // Act
        Activity result = activityService.updateActivity(1, updatedActivity, updatedDetail, null);

        // Assert
        assertThat(result.getTitle()).isEqualTo("更新タイトル");
        assertThat(result.getActivityDate().toLocalDate()).isEqualTo(LocalDate.of(2023, 10, 2));
        assertThat(result.getActivityDetail().getDistanceKm()).isEqualTo(10.0);

        verify(activityRepository, times(1)).findById(1);
        verify(activityRepository, times(1)).save(testActivity);
    }

    @Test
    void testFindAllActivitiesByUser() {
        // Arrange
        when(activityRepository.findByUserOrderByActivityDateDesc(testUser))
                .thenReturn(List.of(testActivity));

        // Act
        List<Activity> result = activityService.findAllActivitiesByUser(testUser);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("初めての登山");
    }

    @Test
    void testGetActivityStats() {
        // Arrange
        when(activityRepository.countByUser(testUser)).thenReturn(5L);
        when(activityRepository.sumDistanceByUser(testUser)).thenReturn(25.5);
        when(activityRepository.sumElevationGainByUser(testUser)).thenReturn(1200L);
        when(activityRepository.sumDurationMinutesByUser(testUser)).thenReturn(500L);

        // Act
        ActivityStatsDto stats = activityService.getActivityStats(testUser);

        // Assert
        assertThat(stats.getTotalActivities()).isEqualTo(5L);
        assertThat(stats.getTotalDistance()).isEqualTo(25.5);
        assertThat(stats.getTotalElevation()).isEqualTo(1200L);
        assertThat(stats.getTotalDuration()).isEqualTo(500L);
    }

    @Test
    void testGetYearlyStats() {
        // Arrange
        Activity a1 = new Activity();
        a1.setActivityDate(Date.valueOf(LocalDate.of(2023, 5, 1)));
        ActivityDetail d1 = new ActivityDetail();
        d1.setDistanceKm(10.0);
        d1.setElevationGain(500);
        a1.setActivityDetail(d1);

        Activity a2 = new Activity();
        a2.setActivityDate(Date.valueOf(LocalDate.of(2023, 8, 15)));
        ActivityDetail d2 = new ActivityDetail();
        d2.setDistanceKm(5.0);
        d2.setElevationGain(300);
        a2.setActivityDetail(d2);

        Activity a3 = new Activity();
        a3.setActivityDate(Date.valueOf(LocalDate.of(2022, 11, 1)));
        ActivityDetail d3 = new ActivityDetail();
        d3.setDistanceKm(15.0);
        d3.setElevationGain(1000);
        a3.setActivityDetail(d3);

        List<Activity> mockActivities = List.of(a1, a2, a3);
        when(activityRepository.findByUserOrderByActivityDateDesc(testUser)).thenReturn(mockActivities);

        // Act
        List<YearlyStatsDto> result = activityService.getYearlyStats(testUser);

        // Assert
        assertThat(result).hasSize(2);

        // 2022
        assertThat(result.get(0).getYear()).isEqualTo(2022);
        assertThat(result.get(0).getTotalActivities()).isEqualTo(1);
        assertThat(result.get(0).getTotalDistance()).isEqualTo(15.0);
        assertThat(result.get(0).getTotalElevation()).isEqualTo(1000);

        // 2023
        assertThat(result.get(1).getYear()).isEqualTo(2023);
        assertThat(result.get(1).getTotalActivities()).isEqualTo(2);
        assertThat(result.get(1).getTotalDistance()).isEqualTo(15.0);
        assertThat(result.get(1).getTotalElevation()).isEqualTo(800);
    }

    @Test
    void testCreateActivity_WithImage() throws Exception {
        // Arrange
        MultipartFile mockFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test data".getBytes());

        when(activityRepository.save(any(Activity.class))).thenAnswer(i -> {
            Activity saved = i.getArgument(0);
            saved.setActivityId(2);
            return saved;
        });

        // Act
        try (MockedStatic<java.nio.file.Files> mockedFiles = mockStatic(java.nio.file.Files.class)) {
            Activity result = activityService.createActivity(testActivity, testDetail, mockFile);

            // Assert
            assertThat(result.getActivityId()).isEqualTo(2);
            assertThat(result.getImageName()).isNotNull();
            assertThat(result.getImageName()).endsWith(".jpg");
            mockedFiles.verify(
                    () -> java.nio.file.Files.copy(any(java.io.InputStream.class), any(java.nio.file.Path.class)),
                    times(1));
        }
    }

    @Test
    void testUpdateActivity_WithImage() throws Exception {
        Activity updatedActivity = new Activity();
        updatedActivity.setTitle("更新タイトル");
        updatedActivity.setActivityDate(Date.valueOf(LocalDate.of(2023, 10, 2)));

        ActivityDetail updatedDetail = new ActivityDetail();
        updatedDetail.setDistanceKm(10.0);

        testActivity.setActivityDetail(testDetail);

        when(activityRepository.findById(1)).thenReturn(Optional.of(testActivity));
        when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

        MultipartFile mockFile = new MockMultipartFile(
                "image", "update.png", "image/png", "test data".getBytes());

        try (MockedStatic<java.nio.file.Files> mockedFiles = mockStatic(java.nio.file.Files.class)) {
            Activity result = activityService.updateActivity(1, updatedActivity, updatedDetail, mockFile);

            assertThat(result.getTitle()).isEqualTo("更新タイトル");
            assertThat(result.getImageName()).isNotNull();
            assertThat(result.getImageName()).endsWith(".png");
            mockedFiles.verify(
                    () -> java.nio.file.Files.copy(any(java.io.InputStream.class), any(java.nio.file.Path.class)),
                    times(1));
        }
    }

    @Test
    void testUpdateActivity_NotFound() {
        when(activityRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> activityService.updateActivity(999, new Activity(), new ActivityDetail(), null));
    }

    @Test
    void testGetActivitiesByUserWithSort() {
        when(activityRepository.findByUser(eq(testUser), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(testActivity));

        activityService.getActivitiesByUserWithSort(testUser, "date_asc");
        verify(activityRepository).findByUser(testUser,
                org.springframework.data.domain.Sort.by("activityDate").ascending());

        activityService.getActivitiesByUserWithSort(testUser, "elevation_desc");
        verify(activityRepository).findByUser(testUser,
                org.springframework.data.domain.Sort.by("activityDetail.maxElevation").descending());

        activityService.getActivitiesByUserWithSort(testUser, "elevation_asc");
        verify(activityRepository).findByUser(testUser,
                org.springframework.data.domain.Sort.by("activityDetail.maxElevation").ascending());

        activityService.getActivitiesByUserWithSort(testUser, "date_desc");
        verify(activityRepository).findByUser(testUser,
                org.springframework.data.domain.Sort.by("activityDate").descending());

        assertThat(activityService.getActivitiesByUserWithSort(null, "date_asc")).isEmpty();
    }

    @Test
    void testFindTop3ActivitiesByUser() {
        when(activityRepository.findTop3ByUserOrderByActivityDateDesc(testUser)).thenReturn(List.of(testActivity));
        List<Activity> result = activityService.findTop3ActivitiesByUser(testUser);
        assertThat(result).hasSize(1);
        assertThat(activityService.findTop3ActivitiesByUser(null)).isEmpty();
    }

    @Test
    void testFindActivityById() {
        when(activityRepository.findById(1)).thenReturn(Optional.of(testActivity));
        assertThat(activityService.findActivityById(1)).isPresent();
        assertThat(activityService.findActivityById(null)).isEmpty();
    }

    @Test
    void testDeleteActivity() {
        activityService.deleteActivity(1);
        verify(activityRepository).deleteById(1);
    }

    @Test
    void testGetActivitiesByUserAndMountain() {
        when(activityRepository.findByUserAndMountainOrderByActivityDateDesc(testUser, testMountain))
                .thenReturn(List.of(testActivity));
        List<Activity> result = activityService.getActivitiesByUserAndMountain(testUser, testMountain);
        assertThat(result).hasSize(1);
        assertThat(activityService.getActivitiesByUserAndMountain(null, testMountain)).isEmpty();
        assertThat(activityService.getActivitiesByUserAndMountain(testUser, null)).isEmpty();
    }

    @Test
    void testGenerateNewFileName() {
        String result = activityService.generateNewFileName("myphoto.jpg");
        assertThat(result).endsWith(".jpg");
        assertThat(result).isNotEqualTo("myphoto.jpg");
        assertThat(result.split("\\.")).hasSize(2);
    }
}
