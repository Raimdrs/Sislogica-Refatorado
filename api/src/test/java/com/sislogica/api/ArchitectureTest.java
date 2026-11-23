package com.sislogica.api;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Teste de Arquitetura (Clean Architecture / Ports & Adapters).
 * Garante que o domínio permaneça 100% puro e isolado de frameworks, banco de dados, gRPC e camadas externas.
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
                "javax.sql..",
                "io.grpc..",
                "com.google.protobuf.."
            )
            .because("O domínio deve ser 100% puro e agnóstico a frameworks, banco de dados e gRPC.");

    @ArchTest
    public static final ArchRule dominio_nao_deve_depender_de_camadas_externas =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..infrastructure..",
                "..application..",
                "..controller..",
                "..service..",
                "..dto.."
            )
            .because("As regras de domínio não podem conhecer detalhes de aplicação, API ou infraestrutura.");

    @ArchTest
    public static final ArchRule servicos_nao_devem_depender_de_infraestrutura =
        noClasses().that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .because("A camada de serviço deve depender apenas das portas definidas no domínio.");

    @ArchTest
    public static final ArchRule dtos_nao_devem_depender_de_infraestrutura =
        noClasses().that().resideInAPackage("..dto..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .because("DTOs devem mapear a partir do domínio, nunca depender de entidades Panache/JPA.");
}
