package indi.mofan.member.service;

import indi.mofan.member.bean.Member;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员服务接口
 * @author mofan
 * @date 2025/1/15 10:00
 */
public interface MemberService {
    
    /**
     * 根据ID获取会员信息
     */
    Member getMemberById(Long id);
    
    /**
     * 获取所有会员列表
     */
    List<Member> getAllMembers();
    
    /**
     * 创建新会员
     */
    Member createMember(Member member);
    
    /**
     * 更新会员信息
     */
    Member updateMember(Member member);
    
    /**
     * 积分充值
     */
    Member rechargePoints(Long memberId, BigDecimal points);
    
    /**
     * 余额充值
     */
    Member rechargeBalance(Long memberId, BigDecimal amount);
    
    /**
     * 升级会员等级
     */
    Member upgradeMemberLevel(Long memberId);
}