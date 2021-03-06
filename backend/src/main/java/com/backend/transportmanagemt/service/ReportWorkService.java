package com.backend.transportmanagemt.service;

import com.backend.transportmanagemt.config.ApplicationProperties;
import com.backend.transportmanagemt.domain.*;
import com.backend.transportmanagemt.repository.ReportWorkRepository;
import com.backend.transportmanagemt.service.dto.ReportWorkRequestDTO;
import com.backend.transportmanagemt.web.rest.errors.StorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class ReportWorkService {
    private final Logger log = LoggerFactory.getLogger(ReportWorkService.class);

    @Autowired
    ReportWorkRepository reportWorkRepository;

    @Autowired
    UserService userService;

    @Autowired
    ApplicationProperties properties;

    private List<String> creatPathFile(MultipartFile[] files) {
        Date date = new Date();
        List<String> listImage = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            try {
                if (file.isEmpty()) {
                    throw new StorageException("Failed to store empty file " + filename);
                }
                if (filename.contains("..")) {
                    throw new StorageException("Cannot store file with relative path outside current directory " + filename);
                }
                try (InputStream inputStream = file.getInputStream()) {
                    Path rootLocation = Paths.get(properties.getBaseDir() + File.separator + "image"
                        + File.separator + date.getYear() + File.separator + date.getMonth() + File.separator + date.getDate());
                    Files.createDirectories(rootLocation);
                    Files.copy(inputStream, rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                    String url = properties.getBaseUrl() + File.separator + "image"
                        + File.separator + date.getYear() + File.separator + date.getMonth() + File.separator + date.getDate() + File.separator + filename;
                    listImage.add(url);
                }
            } catch (IOException e) {
                throw new StorageException("Failed to store file " + filename, e);
            }
        }
        return listImage;
    }

    public ReportWork create(ReportWorkRequestDTO requestDTO, MultipartFile[] images) throws JsonProcessingException {
        User user = userService.getUserWithAuthorities().orElse(null);
        ReportWork reportWork = new ReportWork();
        reportWork.setCreatedUser(user);
        reportWork.setAddressStart(requestDTO.getAddressStart());
        reportWork.setAddressEnd(requestDTO.getAddressEnd());
        reportWork.setDescription(requestDTO.getDescription());
        reportWork.setPhoneCustomer(requestDTO.getPhoneCustomer());
        reportWork.setLicensePlate(requestDTO.getLicensePlate());
        reportWork.setNameCustomer(requestDTO.getNameCustomer());
        reportWork.setTotalMoney(requestDTO.getTotalMoney());
        reportWork.setImplementationDate(requestDTO.getImplementationDate().toInstant());
        Set<ReportWorkersDetail> workersDetails = new HashSet<>();
        for(Long item: requestDTO.getWorkersDetailRequestDTOS()) {
            ReportWorkersDetail workersDetail = new ReportWorkersDetail();
            User worker = userService.findById(item).orElse(null);
            workersDetail.setWorker(worker);
            workersDetails.add(workersDetail);
        }
        reportWork.setWorkers(workersDetails);

        if (images != null && images.length > 0) {
            List<String> listImages = creatPathFile(images);
            ObjectMapper mapper = new ObjectMapper();
            String listImagesLink = mapper.writeValueAsString(listImages);
            reportWork.setImages(listImagesLink);
        }

        ReportWork result = reportWorkRepository.save(reportWork);
        return result;
    }

    public Page<ReportWork> filter(ReportWorkRequestDTO requestDTO,String login, String name, Date createdDateFrom, Date createdDateTo, Date implementationDateFrom, Date implementationDateTo, Pageable pageable) {
        log.debug("call filter method {}", requestDTO);
        Boolean isAdmin = false;
        Instant createdDateStart = null;
        Instant createdDateEnd = null;
        Instant implementationDateStart = null;
        Instant implementationDateEnd = null;

        Calendar calendar = Calendar.getInstance();
        if (createdDateFrom != null) {
            calendar.setTime(createdDateFrom);
            createdDateStart = calendar.getTime().toInstant();
        }
        if (createdDateTo != null) {
            calendar.setTime(createdDateTo);
            calendar.add(Calendar.DATE, 1);
            createdDateEnd =  calendar.getTime().toInstant();
        }
        // ================================= //
        if (implementationDateFrom != null) {
            calendar.setTime(implementationDateFrom);
            implementationDateStart = calendar.getTime().toInstant();
        }
        if (implementationDateTo != null) {
            calendar.setTime(implementationDateTo);
            calendar.add(Calendar.DATE, 1);
            implementationDateEnd =  calendar.getTime().toInstant();
        }

        User user = userService.getUserWithAuthorities().orElse(null);
        for (Authority authority: user.getAuthorities()) {
            if (authority.getName().equals("ROLE_ADMIN")) {
                isAdmin = true;
            }
        }
        System.out.println("================================");
        System.out.println(isAdmin);
        if (isAdmin) {
            return reportWorkRepository.filterForAdmin(login, name, createdDateStart, createdDateEnd, implementationDateStart, implementationDateEnd, pageable);
        } else  {
            return reportWorkRepository.filterForUser(user.getLogin(), name, createdDateStart, createdDateEnd, implementationDateStart, implementationDateEnd, pageable);
        }
    }

    public Optional<ReportWork> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return reportWorkRepository.findById(id);
    }
}
