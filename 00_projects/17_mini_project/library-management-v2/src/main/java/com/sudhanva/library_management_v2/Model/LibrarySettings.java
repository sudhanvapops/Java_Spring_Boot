package com.sudhanva.library_management_v2.Model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.sudhanva.library_management_v2.enums.Setting.SettingKey;
import com.sudhanva.library_management_v2.enums.Setting.SettingValueType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LibrarySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    @Enumerated(EnumType.STRING)
    private SettingKey settingKey;

    @Column(nullable = false)
    private String settingValue;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SettingValueType valueType;

    private String description;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
