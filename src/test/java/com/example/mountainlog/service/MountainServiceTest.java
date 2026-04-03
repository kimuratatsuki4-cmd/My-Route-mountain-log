package com.example.mountainlog.service;

import com.example.mountainlog.dto.MountainMapDto;
import com.example.mountainlog.entity.Activity;
import com.example.mountainlog.entity.ExperienceLevel;
import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.repository.ActivityRepository;
import com.example.mountainlog.repository.MountainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class MountainServiceTest {

    @Mock
    private MountainRepository mountainRepository;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private MountainService mountainService;

    private User testUser;
    private Mountain mountainTokyo;
    private Mountain mountainSaitama;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setAddress("東京都新宿区");
        testUser.setExperienceLevel(ExperienceLevel.BEGINNER);

        mountainTokyo = new Mountain();
        mountainTokyo.setMountainId(1);
        mountainTokyo.setName("高尾山");
        mountainTokyo.setPrefecture("東京都");
        mountainTokyo.setDifficulty("EASY");
        mountainTokyo.setIsHyakumeizan(false);

        mountainSaitama = new Mountain();
        mountainSaitama.setMountainId(2);
        mountainSaitama.setName("武甲山");
        mountainSaitama.setPrefecture("埼玉県");
        mountainSaitama.setDifficulty("MODERATE");
        mountainSaitama.setIsHyakumeizan(false);
    }

    @Test
    void testSearchMountains_Keyword() {
        // Arrange
        when(mountainRepository.searchByKeyword(eq("高尾"), any(Pageable.class)))
                .thenReturn(List.of(mountainTokyo));

        // Act
        List<Mountain> result = mountainService.searchMountains(" 高尾 ");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("高尾山");
    }

    @Test
    void testSearchMountains_EmptyKeyword() {
        // Act
        List<Mountain> result = mountainService.searchMountains("   ");

        // Assert
        assertThat(result).isEmpty();
        verify(mountainRepository, never()).searchByKeyword(anyString(), any(Pageable.class));
    }

    @Test
    void testSearchMountainsWithFilter() {
        // Arrange
        Page<Mountain> page = new PageImpl<>(List.of(mountainTokyo));
        Pageable pageable = PageRequest.of(0, 10);

        when(mountainRepository.searchMountainsWithFilter(
                eq("高尾"), eq("東京都"), eq("EASY"), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        // Act
        Page<Mountain> result = mountainService.searchMountainsWithFilter(
                "高尾", "東京都", "EASY", null, null, pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void testFindById() {
        // Arrange
        when(mountainRepository.findById(1)).thenReturn(Optional.of(mountainTokyo));

        // Act
        Optional<Mountain> result = mountainService.findById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("高尾山");
    }

    @Test
    void testGetRecommendedMountains_BeginnerInTokyo() {
        // Arrange
        // ユーザーは初心者(EASY)で東京都在住。既登はなし。
        when(activityRepository.findVisitedMountainIdsByUser(testUser)).thenReturn(new ArrayList<>());

        // 全山リスト
        List<Mountain> allMountains = new ArrayList<>();
        allMountains.add(mountainTokyo); // EASY, 東京 -> おすすめ対象
        allMountains.add(mountainSaitama); // MODERATE, 埼玉 -> 初心者なので除外

        when(mountainRepository.findAll()).thenReturn(allMountains);

        // Act
        List<Mountain> result = mountainService.getRecommendedMountains(testUser);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("高尾山");
    }

    @Test
    void testGetMountainMapData() {
        // Arrange
        when(mountainRepository.findAll()).thenReturn(List.of(mountainTokyo, mountainSaitama));

        Activity mockActivity = new Activity();
        mockActivity.setMountain(mountainTokyo);
        mockActivity.setActivityDate(Date.valueOf(LocalDate.of(2023, 10, 1)));

        when(activityRepository.findByUserAndMountainNotNull(testUser))
                .thenReturn(List.of(mockActivity));

        // Act
        List<MountainMapDto> result = mountainService.getMountainMapData(testUser);

        // Assert
        assertThat(result).hasSize(2);

        // 高尾山 (ID: 1) is climbed
        MountainMapDto tokyoDto = result.stream().filter(m -> m.getId() == 1).findFirst().get();
        assertThat(tokyoDto.getIsClimbed()).isTrue();
        assertThat(tokyoDto.getLastClimbDate()).isEqualTo(LocalDate.of(2023, 10, 1));

        // 武甲山 (ID: 2) is NOT climbed
        MountainMapDto saitamaDto = result.stream().filter(m -> m.getId() == 2).findFirst().get();
        assertThat(saitamaDto.getIsClimbed()).isFalse();
        assertThat(saitamaDto.getLastClimbDate()).isNull();
    }
}
