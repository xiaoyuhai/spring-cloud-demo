package indi.mofan.member.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import indi.mofan.member.bean.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 会员服务实现类
 * @author mofan
 * @date 2025/1/15 10:00
 */
@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    // 模拟数据库存储
    private final Map<Long, Member> memberStorage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public MemberServiceImpl() {
        // 初始化一些测试数据
        initTestData();
    }

    private void initTestData() {
        Member member1 = new Member();
        member1.setId(1L);
        member1.setUsername("user001");
        member1.setNickname("张三");
        member1.setEmail("zhangsan@example.com");
        member1.setPhone("13800138001");
        member1.setLevel(2);
        member1.setPoints(new BigDecimal("1500"));
        member1.setBalance(new BigDecimal("500.00"));
        member1.setCreateTime(LocalDateTime.now().minusDays(30));
        member1.setUpdateTime(LocalDateTime.now());
        member1.setStatus(1);
        memberStorage.put(1L, member1);

        Member member2 = new Member();
        member2.setId(2L);
        member2.setUsername("user002");
        member2.setNickname("李四");
        member2.setEmail("lisi@example.com");
        member2.setPhone("13800138002");
        member2.setLevel(1);
        member2.setPoints(new BigDecimal("800"));
        member2.setBalance(new BigDecimal("200.00"));
        member2.setCreateTime(LocalDateTime.now().minusDays(15));
        member2.setUpdateTime(LocalDateTime.now());
        member2.setStatus(1);
        memberStorage.put(2L, member2);

        idGenerator.set(3L);
    }

    @Override
    public Member getMemberById(Long id) {
        log.info("获取会员信息，ID: {}", id);
        Member member = memberStorage.get(id);
        if (member == null) {
            throw new RuntimeException("会员不存在，ID: " + id);
        }
        return member;
    }

    @Override
    public List<Member> getAllMembers() {
        log.info("获取所有会员列表");
        return new ArrayList<>(memberStorage.values());
    }

    @Override
    public Member createMember(Member member) {
        log.info("创建新会员: {}", member.getUsername());
        Long id = idGenerator.getAndIncrement();
        member.setId(id);
        member.setLevel(1); // 默认普通会员
        member.setPoints(BigDecimal.ZERO); // 默认积分为0
        member.setBalance(BigDecimal.ZERO); // 默认余额为0
        member.setCreateTime(LocalDateTime.now());
        member.setUpdateTime(LocalDateTime.now());
        member.setStatus(1); // 默认正常状态
        
        memberStorage.put(id, member);
        return member;
    }

    @Override
    public Member updateMember(Member member) {
        log.info("更新会员信息，ID: {}", member.getId());
        Member existingMember = memberStorage.get(member.getId());
        if (existingMember == null) {
            throw new RuntimeException("会员不存在，ID: " + member.getId());
        }
        
        member.setUpdateTime(LocalDateTime.now());
        memberStorage.put(member.getId(), member);
        return member;
    }

    @Override
    @SentinelResource(value = "rechargePoints", blockHandler = "rechargePointsBlockHandler")
    public Member rechargePoints(Long memberId, BigDecimal points) {
        log.info("会员积分充值，ID: {}, 充值积分: {}", memberId, points);
        Member member = getMemberById(memberId);
        
        if (points.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值积分必须大于0");
        }
        
        BigDecimal newPoints = member.getPoints().add(points);
        member.setPoints(newPoints);
        member.setUpdateTime(LocalDateTime.now());
        
        memberStorage.put(memberId, member);
        return member;
    }

    @Override
    public Member rechargeBalance(Long memberId, BigDecimal amount) {
        log.info("会员余额充值，ID: {}, 充值金额: {}", memberId, amount);
        Member member = getMemberById(memberId);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }
        
        BigDecimal newBalance = member.getBalance().add(amount);
        member.setBalance(newBalance);
        member.setUpdateTime(LocalDateTime.now());
        
        memberStorage.put(memberId, member);
        return member;
    }

    @Override
    public Member upgradeMemberLevel(Long memberId) {
        log.info("升级会员等级，ID: {}", memberId);
        Member member = getMemberById(memberId);
        
        if (member.getLevel() >= 4) {
            throw new RuntimeException("已是最高等级会员");
        }
        
        member.setLevel(member.getLevel() + 1);
        member.setUpdateTime(LocalDateTime.now());
        
        memberStorage.put(memberId, member);
        return member;
    }

    /**
     * 积分充值限流降级处理
     */
    public Member rechargePointsBlockHandler(Long memberId, BigDecimal points, BlockException ex) {
        log.warn("积分充值被限流，会员ID: {}, 充值积分: {}", memberId, points);
        throw new RuntimeException("积分充值服务繁忙，请稍后重试");
    }
}