package com.sudhanva.library_management_v2.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sudhanva.library_management_v2.Model.LibrarySettings;
import com.sudhanva.library_management_v2.enums.Setting.SettingKey;

@Repository
public interface LibrarySettingsRepo extends JpaRepository<LibrarySettings,Long>{

    Optional<LibrarySettings> findBySettingKey(SettingKey settingKey);
}
