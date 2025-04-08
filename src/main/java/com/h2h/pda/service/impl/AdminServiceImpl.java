package com.h2h.pda.service.impl;

import com.h2h.pda.entity.BroadcastMessageEntity;
import com.h2h.pda.pojo.StatisticsDto;
import com.h2h.pda.pojo.group.GroupsEntityWrapper;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;
import com.h2h.pda.pojo.service.ServiceMeta;
import com.h2h.pda.pojo.service.ServiceType;
import com.h2h.pda.repository.*;
import com.h2h.pda.service.api.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AdminServiceImpl implements AdminService {
    private Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    BroadcastMessageRepository broadcastMessageRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    GroupsRepository groupsRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    CredentialRepository credentialRepository;

    @Override
    @PreAuthorize("@securityService.hasAdmin(authentication)")
    public BroadcastMessageEntity newBroadcastMessage(BroadcastMessageEntity messageEntity) {
        messageEntity.setIsDeleted(false);
        messageEntity.setMessageId(UUID.randomUUID().toString());
        return broadcastMessageRepository.save(messageEntity);
    }

    @Override
    public List<BroadcastMessageEntity> activeMessages() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Timestamp startts = null;
        Timestamp endts = null;
        Date startdate = null;
        Date enddate = null;

        List<BroadcastMessageEntity> messages = new ArrayList<>();

        for (BroadcastMessageEntity broadcastMessageEntity : broadcastMessageRepository.findAll()) {
            try {
                startdate = broadcastMessageEntity.getStartDate();
                startts = new Timestamp(startdate.getTime());
                enddate = broadcastMessageEntity.getEndDate();
                endts = new Timestamp(enddate.getTime());
            } catch (Exception e) {
                log.warn("Error: {}", e.getMessage());
                continue;
            }

            if (ts.after(startts) && ts.before(endts)) {
                messages.add(broadcastMessageEntity);
            }
        }

        return messages;
    }

    @Override
    @PreAuthorize("@securityService.hasAdmin(authentication)")
    public List<BroadcastMessageEntity> allMessages() {
        return StreamSupport.stream(broadcastMessageRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@securityService.hasAdmin(authentication)")
    public void deleteMessage(String id) {
        broadcastMessageRepository.deleteById(id);
    }

    @Override
    public StatisticsDto statistics() {
        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setTotalServices(serviceRepository.countByDeletedAtIsNull());
        statisticsDto.setTotalGroups(groupsRepository.countByDeletedAtIsNull());
        statisticsDto.setTotalActions(actionRepository.countByAll());
        statisticsDto.setTotalVault(credentialRepository.count());

        List<ServiceType> terminals = Arrays.stream(ServiceType.values()).filter(t -> t.getMeta() == ServiceMeta.TERMINAL).collect(Collectors.toList());
        List<ServiceType> databases = Arrays.stream(ServiceType.values()).filter(t -> t.getMeta() == ServiceMeta.DATABASE).collect(Collectors.toList());

        statisticsDto.setTotalTerminalServices(serviceRepository.countByServiceType(terminals));
        statisticsDto.setTotalDatabaseServices(serviceRepository.countByServiceType(databases));
        statisticsDto.setTotalOnlineSession(sessionRepository.countByLive());
        statisticsDto.setLatestServices(serviceRepository.getTop5ByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream().map(s -> new ServiceEntityWrapper(s)).collect(Collectors.toList()));
        statisticsDto.setMostActiveServices(serviceRepository.getMostActiveServices(PageRequest.of(0, 5))
                .stream().map(s -> new ServiceEntityWrapper(s)).collect(Collectors.toList()));
        statisticsDto.setLatestGroups(groupsRepository.getTop5ByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream().map(s -> new GroupsEntityWrapper(s)).collect(Collectors.toList()));

        statisticsDto.setTotalMysql(serviceRepository.countByServiceTypeId(ServiceType.MYSQL));
        statisticsDto.setTotalPostgresql(serviceRepository.countByServiceTypeId(ServiceType.POSTGRESQL));
        statisticsDto.setTotalMssql(serviceRepository.countByServiceTypeId(ServiceType.MSSQL));
        statisticsDto.setTotalOracle(serviceRepository.countByServiceTypeId(ServiceType.ORACLE));
        statisticsDto.setTotalSsh(serviceRepository.countByServiceTypeId(ServiceType.SSH));
        statisticsDto.setTotalTelnet(serviceRepository.countByServiceTypeId(ServiceType.TELNET));
        statisticsDto.setTotalRdp(serviceRepository.countByServiceTypeId(ServiceType.RDP));
        statisticsDto.setTotalVnc(serviceRepository.countByServiceTypeId(ServiceType.VNC));

        return statisticsDto;
    }
}