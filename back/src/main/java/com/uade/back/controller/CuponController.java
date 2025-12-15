package com.uade.back.controller;

import com.uade.back.dto.cupon.CrearCuponRequest;
import com.uade.back.dto.cupon.CuponPageResponse;
import com.uade.back.dto.cupon.ValidarCuponRequest;
import com.uade.back.dto.cupon.ValidarCuponResponse;
import com.uade.back.entity.Cupon;
import com.uade.back.service.cupon.CuponService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing coupons.
 */
@RestController
@RequestMapping("/api/v1/cupones")
@RequiredArgsConstructor
public class CuponController {

    private final CuponService cuponService;

    /**
     * Creates a new coupon.
     *
     * @param request The coupon creation details.
     * @return The created coupon.
     */
    @PostMapping
    public ResponseEntity<Cupon> crearCupon(
        @RequestBody CrearCuponRequest request
    ) {
        return ResponseEntity.ok(cuponService.crearCupon(request));
    }

    /**
     * Retrieves all coupons with optional filtering and pagination.
     *
     * @param codigo Optional coupon code filter.
     * @param activo Optional active status filter.
     * @param page   Page number (default 0).
     * @param size   Page size (default 20).
     * @return A paginated response of coupons.
     */
    @GetMapping
    public ResponseEntity<CuponPageResponse> getAllCupones(
        @RequestParam(required = false) String codigo,
        @RequestParam(required = false) Boolean activo,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
            cuponService.getAllCupones(codigo, activo, page, size)
        );
    }

    /**
     * Updates an existing coupon.
     *
     * @param id      The ID of the coupon to update.
     * @param request The coupon update details.
     * @return The updated coupon response.
     */
    @PutMapping("/{id}")
    public ResponseEntity<com.uade.back.dto.cupon.CuponResponse> updateCupon(
        @PathVariable Integer id,
        @RequestBody com.uade.back.dto.cupon.CuponUpdateRequest request
    ) {
        return ResponseEntity.ok(cuponService.updateCupon(id, request));
    }

    /**
     * Validates a coupon code.
     *
     * @param request The validation request containing the coupon code.
     * @return The validation response indicating validity and discount percentage.
     */
    @PostMapping("/validar")
    public ResponseEntity<ValidarCuponResponse> validarCupon(
        @RequestBody ValidarCuponRequest request
    ) {
        Optional<Cupon> cuponOpt = cuponService.validarCupon(
            request.getCodigo()
        );
        if (cuponOpt.isPresent()) {
            return ResponseEntity.ok(
                new ValidarCuponResponse(
                    true,
                    cuponOpt.get().getPorcentajeDescuento()
                )
            );
        } else {
            return ResponseEntity.ok(new ValidarCuponResponse(false, null));
        }
    }
}
