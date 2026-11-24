package ifc.edu.br.bikes.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class JasperService {
    public byte[] generatePdf() {
        try {
            // load compiled Jasper file from resources
            ClassPathResource resource = new ClassPathResource("reports/locacoes_report.jasper");
            try (InputStream is = resource.getInputStream()) {
                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(is);
                // parameters (path for images)
                Map<String, Object> params = new HashMap<>();
                params.put("IMG_DIR", new ClassPathResource("reports/").getURL().toString());
                // using empty datasource because the query is embedded in the .jasper file
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
                return JasperExportManager.exportReportToPdf(jasperPrint);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório", e);
        }
    }
}
