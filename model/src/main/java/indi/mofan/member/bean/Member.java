package indi.mofan.member.bean;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员实体类
 * @author mofan
 * @date 2025/1/15 10:00
 */
@Getter
@Setter
public class Member {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private Integer level; // 会员等级：1-普通会员，2-银牌会员，3-金牌会员，4-钻石会员
    private BigDecimal points; // 积分
    private BigDecimal balance; // 余额
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status; // 状态：1-正常，0-禁用
}