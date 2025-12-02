package lenicorp.metier.association.controller.services;

import lenicorp.admin.archive.model.dtos.response.ReadDocDTO;
import lenicorp.admin.archive.model.entities.Document;
import lenicorp.metier.association.model.dtos.CreateAssociationDTO;
import lenicorp.metier.association.model.dtos.ReadAssociationDTO;
import lenicorp.metier.association.model.dtos.UpdateAssociationDTO;
import lenicorp.metier.association.model.entities.Association;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IAssociationService
{
    Association createAssociation(CreateAssociationDTO dto) throws IOException;
    Association updateAssociation(UpdateAssociationDTO dto);
    Page<ReadAssociationDTO> searchAssociations(String key, PageRequest pageable);
    List<ReadAssociationDTO> searchAssociationsList(String key);
    byte[] getAssoLogo(Long assoId);
    ReadAssociationDTO findById(Long assoId);
    Association createAssociation(CreateAssociationDTO dto, File logo) throws IOException;
    byte[] generateFicheAdhesion(Long assoId) throws Exception;
}
