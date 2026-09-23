package com.sislogica.api;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Teste de Arquitetura exigido na Sprint 1 (DIM0547).
 * Garante a regra fundamental da Clean Architecture:
 * Camadas internas (domínio) não podem depender de frameworks externos ou adaptadores de infraestrutura.
 */
@AnalyzeClasses(packages = "com.sislogica.api", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    public static final ArchRule dominio_nao_deve_depender_de_frameworks =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "io.quarkus..",
                "jakarta.persistence..",
                "jakarta.ws.rs..",
                "org.hibernate..",
                "java.sql..",
                "javax.sql.."
            )
            .because("O domínio deve ser 100% puro e agnóstico a frameworks e banco de dados.");

    @ArchTest
    public static final ArchRule dominio_nao_deve_depender_de_camadas_externas =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..infrastructure..",
                "..application.."
            )
            .because("As regras de domínio não podem conhecer detalhes de aplicação ou infraestrutura.");
}

