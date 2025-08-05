package indi.mofan.member.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import indi.mofan.common.R;
import indi.mofan.member.bean.Member;
import indi.mofan.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员控制器
 * @author mofan
 * @date 2025/1/15 10:00
 */
@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 根据ID获取会员信息
     */
    @GetMapping("/{id}")
    public R getMemberById(@PathVariable("id") Long id) {
        Member member = memberService.getMemberById(id);
        return R.ok("获取会员信息成功", member);
    }

    /**
     * 获取所有会员列表
     */
    @GetMapping("/list")
    public R getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return R.ok("获取会员列表成功", members);
    }

    /**
     * 创建新会员
     */
    @PostMapping("/create")
    public R createMember(@RequestBody Member member) {
        Member createdMember = memberService.createMember(member);
        return R.ok("创建会员成功", createdMember);
    }

    /**
     * 更新会员信息
     */
    @PutMapping("/update")
    public R updateMember(@RequestBody Member member) {
        Member updatedMember = memberService.updateMember(member);
        return R.ok("更新会员信息成功", updatedMember);
    }

    /**
     * 会员积分充值
     */
    @PostMapping("/recharge-points")
    @SentinelResource(value = "recharge-points", fallback = "rechargePointsFallback")
    public R rechargePoints(@RequestParam("memberId") Long memberId,
                           @RequestParam("points") BigDecimal points) {
        Member member = memberService.rechargePoints(memberId, points);
        return R.ok("积分充值成功", member);
    }

    /**
     * 积分充值降级方法
     */
    public R rechargePointsFallback(Long memberId, BigDecimal points, Throwable throwable) {
        log.error("积分充值失败，会员ID: {}, 充值积分: {}, 异常: {}", memberId, points, throwable.getMessage());
        return R.error(500, "积分充值服务暂时不可用，请稍后重试");
    }

    /**
     * 会员余额充值
     */
    @PostMapping("/recharge-balance")
    public R rechargeBalance(@RequestParam("memberId") Long memberId,
                            @RequestParam("amount") BigDecimal amount) {
        Member member = memberService.rechargeBalance(memberId, amount);
        return R.ok("余额充值成功", member);
    }

    /**
     * 升级会员等级
     */
    @PostMapping("/upgrade-level")
    public R upgradeMemberLevel(@RequestParam("memberId") Long memberId) {
        Member member = memberService.upgradeMemberLevel(memberId);
        return R.ok("会员等级升级成功", member);
    }
}