package com.paozero.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "xxl_job_group")
public class JobGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String app_name;
    private String title;
    private int address_type;
    private String address_list;
    private Date update_time;

    public JobGroup(String app_name, String title, int address_type, String address_list, Date update_time) {
        this.app_name = app_name;
        this.title = title;
        this.address_type = address_type;
        this.address_list = address_list;
        this.update_time = update_time;
    }
}
