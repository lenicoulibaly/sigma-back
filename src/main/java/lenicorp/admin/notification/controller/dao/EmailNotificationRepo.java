package lenicorp.admin.notification.controller.dao;

import lenicorp.admin.notification.model.entities.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationRepo extends JpaRepository<EmailNotification, Long>
{

}
