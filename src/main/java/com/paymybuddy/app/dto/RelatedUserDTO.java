package com.paymybuddy.app.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatedUserDTO {
    private int id;
    private String name;

    public RelatedUserDTO(int id, String userName) {
        this.id = id;
        this.name = userName;
    }


}
