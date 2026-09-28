package br.com.sislogica.service;

import br.com.sislogica.domain.entity.SilogismoPanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class SilogismoService {

    public List<SilogismoPanacheEntity> buscarPorTurmaPaginado(Long turmaId, int pagina, int tamanho) {
        PanacheQuery<SilogismoPanacheEntity> query = SilogismoPanacheEntity.find("turma.id", turmaId);
        
        return query.page(Page.of(pagina, tamanho)).list();
    }
}
