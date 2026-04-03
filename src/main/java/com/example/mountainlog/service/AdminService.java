package com.example.mountainlog.service;

import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.Role;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.form.AdminUserForm;
import com.example.mountainlog.form.MountainForm;
import com.example.mountainlog.repository.ActivityRepository;
import com.example.mountainlog.repository.MountainRepository;
import com.example.mountainlog.repository.RoleRepository;
import com.example.mountainlog.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final MountainRepository mountainRepository;
    private final ActivityRepository activityRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, MountainRepository mountainRepository,
            ActivityRepository activityRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mountainRepository = mountainRepository;
        this.activityRepository = activityRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getPremiumUsers() {
        return userRepository.countByRole_Name("ROLE_PREMIUM");
    }

    public long getTotalMountains() {
        return mountainRepository.count();
    }

    public long getRecentActivities() {
        Date oneWeekAgo = Date.valueOf(LocalDate.now().minusDays(7));
        return activityRepository.countByActivityDateAfter(oneWeekAgo);
    }

    // --- User Management ---

    public Page<com.example.mountainlog.entity.User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(Objects.requireNonNull(pageable));
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(Objects.requireNonNull(id));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public void saveUserFromAdmin(AdminUserForm form) {
        User user = new User();
        if (form.getUserId() != null) {
            user = userRepository.findById(Objects.requireNonNull(form.getUserId())).orElse(new User());
        }

        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setEnabled(form.getEnabled());

        Role role = roleRepository.findById(Objects.requireNonNull(form.getRoleId())).orElse(null);
        user.setRole(role);

        // パスワードが入力されている（空白のみではない）場合のみ更新
        if (form.getPassword() != null && !form.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Integer id) {
        userRepository.deleteById(Objects.requireNonNull(id));
    }

    public Map<String, Object> getSalesForecastData() {
        // 売上グラフ用ダミーデータ（過去6ヶ月実績 + 今後6ヶ月予測）
        List<String> labels = Arrays.asList("Oct", "Nov", "Dec", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                "Sep");
        List<Integer> actual = Arrays.asList(55000, 68000, 72000, 85000, 94000, 110000, null, null, null, null, null,
                null);
        List<Integer> forecast = Arrays.asList(null, null, null, null, null, 110000, 125000, 140000, 155000, 175000,
                200000, 230000);

        Map<String, Object> data = new HashMap<>();
        data.put("labels", labels);
        data.put("actual", actual);
        data.put("forecast", forecast);
        return data;
    }

    // --- Mountain Master CRUD Methods ---

    public Page<Mountain> getAllMountains(Pageable pageable) {
        return mountainRepository.findAll(Objects.requireNonNull(pageable));
    }

    public Optional<Mountain> getMountainById(Integer id) {
        return mountainRepository.findById(Objects.requireNonNull(id));
    }

    @Transactional
    public void saveMountainFromForm(MountainForm form) {
        Mountain mountain = new Mountain();
        if (form.getMountainId() != null) {
            mountain = mountainRepository.findById(Objects.requireNonNull(form.getMountainId()))
                    .orElse(new Mountain());
        }

        mountain.setName(form.getName());
        mountain.setNameEn(form.getNameEn());
        mountain.setNameKana(form.getNameKana());
        mountain.setElevation(form.getElevation());
        mountain.setPrefecture(form.getPrefecture());
        mountain.setLatitude(form.getLatitude());
        mountain.setLongitude(form.getLongitude());
        mountain.setTypicalDistanceKm(form.getTypicalDistanceKm());
        mountain.setTypicalDurationMinutes(form.getTypicalDurationMinutes());
        mountain.setTypicalElevationGain(form.getTypicalElevationGain());
        mountain.setDifficulty(form.getDifficulty());
        mountain.setDescription(form.getDescription());
        mountain.setIsHyakumeizan(form.getIsHyakumeizan());
        mountain.setImageUrl(form.getImageUrl());
        mountain.setImageCitation(form.getImageCitation());

        mountainRepository.save(mountain);
    }

    @Transactional
    public void deleteMountain(Integer id) {
        mountainRepository.deleteById(Objects.requireNonNull(id));
    }
}
