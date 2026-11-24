package ifc.edu.br.bikes.web.controller;

import ifc.edu.br.bikes.service.JasperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/relatorios")
@Tag(name = "Relatórios", description = "Endpoints de geração de relatórios")
public class RelatorioController {

    private final JasperService jasperService;

    public RelatorioController(JasperService jasperService) {
        this.jasperService = jasperService;
    }

    @Operation(summary = "Gerar relatório de locações em PDF", security = @SecurityRequirement(name = "security"))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/locacoes")
    public void gerarRelatorioLocacoes(HttpServletResponse response) {
        byte[] pdf = jasperService.generatePdf();
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "inline; filename=locacoes_" + System.currentTimeMillis() + ".pdf");
        try {
            response.getOutputStream().write(pdf);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar relatório", e);
        }
    }
}
