package com.swd392.mentorbooking.dto.admin;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.AccountStatusEnum;
import com.swd392.mentorbooking.entity.Enum.GenderEnum;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.entity.Group;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccountInfoAdmin {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime dayOfBirth;
    private GenderEnum gender;
    private String phone;
    private RoleEnum role;
    private String avatar;
    private String className;
    private AccountStatusEnum status;
    private Boolean isDeleted;
    private Group group;


    public static AccountInfoAdmin fromAccount(Account account) {
        AccountInfoAdmin accountInfoAdmin = new AccountInfoAdmin();
        accountInfoAdmin.setId(account.getId());
        accountInfoAdmin.setName(account.getName());
        accountInfoAdmin.setEmail(account.getEmail());
        accountInfoAdmin.setDayOfBirth(account.getDayOfBirth());
        accountInfoAdmin.setGender(account.getGender());
        accountInfoAdmin.setPhone(account.getPhone());
        accountInfoAdmin.setRole(account.getRole());
        accountInfoAdmin.setAvatar(account.getAvatar());
        accountInfoAdmin.setClassName(account.getClassName());
        accountInfoAdmin.setStatus(account.getStatus());
        accountInfoAdmin.setIsDeleted(account.getIsDeleted());
        accountInfoAdmin.setGroup(account.getGroup());
        return accountInfoAdmin;
    }
}
