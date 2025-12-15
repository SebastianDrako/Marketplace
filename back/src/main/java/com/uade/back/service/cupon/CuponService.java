package com.uade.back.service.cupon;

import com.uade.back.dto.cupon.CrearCuponRequest;
import com.uade.back.dto.cupon.CuponPageResponse;
import com.uade.back.dto.cupon.CuponResponse;
import com.uade.back.entity.Cupon;
import com.uade.back.repository.CuponRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing coupons.
 */
@Service
@RequiredArgsConstructor
public class CuponService {

    private final CuponRepository cuponRepository;

    /**
     * Creates a new coupon.
     *
     * @param request The coupon creation request.
     * @return The created coupon entity.
     * @throws IllegalArgumentException if the coupon code already exists.
     */
    @Transactional
    public Cupon crearCupon(CrearCuponRequest request) {
        if (cuponRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("El código de cupón ya existe.");
        }

        Cupon cupon = Cupon.builder()
            .codigo(request.getCodigo())
            .porcentajeDescuento(request.getPorcentajeDescuento())
            .fechaExpiracion(request.getFechaExpiracion())
            .usosMaximos(request.getUsosMaximos())
            .build();

        return cuponRepository.save(cupon);
    }

    /**
     * Validates a coupon by its code.
     *
     * @param codigo The coupon code.
     * @return An Optional containing the coupon if valid, or empty otherwise.
     */
    @Transactional(readOnly = true)
    public Optional<Cupon> validarCupon(String codigo) {
        Optional<Cupon> cuponOpt = cuponRepository.findByCodigo(codigo);

        if (cuponOpt.isEmpty()) {
            return Optional.empty();
        }

        Cupon cupon = cuponOpt.get();

        if (
            !cupon.getActivo() ||
            cupon.getFechaExpiracion().isBefore(Instant.now()) ||
            cupon.getUsosActuales() >= cupon.getUsosMaximos()
        ) {
            return Optional.empty();
        }

        return cuponOpt;
    }

    /**
     * Retrieves coupons with filtering and pagination.
     *
     * @param codigo Optional code to search for.
     * @param activo Optional status to filter by.
     * @param page   Page number.
     * @param size   Page size.
     * @return A response containing a list of coupons and total pages.
     */
    @Transactional(readOnly = true)
    public CuponPageResponse getAllCupones(
        String codigo,
        Boolean activo,
        int page,
        int size
    ) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page,
            size
        );
        Page<Cupon> results = cuponRepository.search(codigo, activo, pageable);
        List<CuponResponse> cupones = results
            .getContent()
            .stream()
            .map(this::toCuponResponse)
            .collect(Collectors.toList());

        return new CuponPageResponse(cupones, results.getTotalPages());
    }

    /**
     * Converts a Cupon entity to a CuponResponse DTO.
     *
     * @param cupon The coupon entity.
     * @return The coupon response DTO.
     */
    private CuponResponse toCuponResponse(Cupon cupon) {
        return CuponResponse.builder()
            .id(cupon.getCuponId())
            .codigo(cupon.getCodigo())
            .porcentajeDescuento(cupon.getPorcentajeDescuento())
            .fechaExpiracion(cupon.getFechaExpiracion())
            .usosMaximos(cupon.getUsosMaximos())
            .usosActuales(cupon.getUsosActuales())
            .activo(cupon.getActivo())
            .build();
    }

    /**
     * Updates an existing coupon.
     *
     * @param id      The ID of the coupon to update.
     * @param request The coupon update request.
     * @return The updated coupon response.
     * @throws RuntimeException if the coupon is not found.
     * @throws IllegalArgumentException if the new code already exists for another coupon.
     */
    @Transactional
    public CuponResponse updateCupon(
        Integer id,
        com.uade.back.dto.cupon.CuponUpdateRequest request
    ) {
        Cupon cupon = cuponRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Cupón no encontrado."));

        if (request.getCodigo() != null) {
            cuponRepository
                .findByCodigo(request.getCodigo())
                .ifPresent(existingCupon -> {
                    if (!existingCupon.getCuponId().equals(id)) {
                        throw new IllegalArgumentException(
                            "El código de cupón ya existe."
                        );
                    }
                });
            cupon.setCodigo(request.getCodigo());
        }
        if (request.getPorcentajeDescuento() != null) {
            cupon.setPorcentajeDescuento(request.getPorcentajeDescuento());
        }
        if (request.getFechaExpiracion() != null) {
            cupon.setFechaExpiracion(request.getFechaExpiracion());
        }
        if (request.getUsosMaximos() != null) {
            cupon.setUsosMaximos(request.getUsosMaximos());
        }
        if (request.getActivo() != null) {
            cupon.setActivo(request.getActivo());
        }

        Cupon updatedCupon = cuponRepository.save(cupon);
        return toCuponResponse(updatedCupon);
    }
}
