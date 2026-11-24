package ifc.edu.br.bikes.service;

import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

@Service
public class JasperService {
    private final DataSource dataSource;

    public JasperService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte[] generatePdf() {
        ClassPathResource reportResource = new ClassPathResource("reports/locacoes_report.jrxml");
        ClassPathResource reportsDirectory = new ClassPathResource("reports/");

        try (InputStream reportStream = reportResource.getInputStream();
             Connection connection = dataSource.getConnection()) {
                // Compile the current template, so its fields and image settings stay in sync.
                JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
                Map<String, Object> params = new HashMap<>();
                params.put("REPORT_ROOT_DIR", reportsDirectory.getURL().toString());

                // The SQL query declared in the report runs on the application's database.
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);
                return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório", e);
        }
    }
}
