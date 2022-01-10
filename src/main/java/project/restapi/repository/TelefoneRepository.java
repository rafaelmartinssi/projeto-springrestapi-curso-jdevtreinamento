package project.restapi.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import project.restapi.model.Telefone;

@Repository
@Transactional
public interface TelefoneRepository extends CrudRepository<Telefone, Long>{

}
