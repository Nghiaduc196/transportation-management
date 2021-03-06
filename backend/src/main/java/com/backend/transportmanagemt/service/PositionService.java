package com.backend.transportmanagemt.service;

import com.backend.transportmanagemt.domain.Position;
import com.backend.transportmanagemt.domain.enums.CommonStatus;
import com.backend.transportmanagemt.repository.PositionRepository;
import com.backend.transportmanagemt.service.dto.PositionRequestDTO;
import com.backend.transportmanagemt.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PositionService {

    private final Logger log = LoggerFactory.getLogger(PositionService.class);

    @Autowired
    PositionRepository positionRepository;

    public Position create(PositionRequestDTO requestDTO) {
        // Kiểm tra trùng tên
        Position checkPositionName = positionRepository.findTopByNameAndStatusNotLike(requestDTO.getName().replaceAll("\\s\\s+", " ").trim(), CommonStatus.DELETED).orElse(null);
        if (checkPositionName != null) {
            throw new BadRequestAlertException("Duplicate name position", "Position", "duplicate");
        }
        Position position = new Position();
        position.setName(requestDTO.getName().replaceAll("\\s\\s+", " ").trim());
        position.setStatus(CommonStatus.ENABLE);
        return positionRepository.save(position);
    }

    public Page<Position> filter(PositionRequestDTO requestDTO, Pageable pageable) {
        return positionRepository.filter(requestDTO.getName(), CommonStatus.DELETED, pageable);
    }

    public Page<Position> getAllByStatusEnable(Pageable pageable) {
        return positionRepository.getAllByStatus(CommonStatus.ENABLE, pageable);
    }

    public Optional<Position> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return positionRepository.findById(id);
    }

    public Optional<Position> delete(Long id) {
        return Optional.of(positionRepository.findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(position -> {
                position.setStatus(CommonStatus.DELETED);
                return position;
            });
    }

    public Optional<Position> update(PositionRequestDTO requestDTO) {
        return Optional.of(positionRepository.findById(requestDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(position -> {
                Position checkPositionName = positionRepository.findTopByNameAndStatusNotLike(requestDTO.getName().replaceAll("\\s\\s+", " ").trim(), CommonStatus.DELETED).orElse(null);
                if (checkPositionName != null) {
                    throw new BadRequestAlertException("Duplicate name position", "Position", "duplicate");
                }
                position.setName(requestDTO.getName().replaceAll("\\s\\s+", " ").trim());
                position.setStatus(requestDTO.getStatus());
                return position;
            });
    }

    public Optional<Position> changeStatus(PositionRequestDTO requestDTO) {
        return Optional.of(positionRepository.findById(requestDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(position -> {
                position.setStatus(requestDTO.getStatus());
                return position;
            });
    }
}
