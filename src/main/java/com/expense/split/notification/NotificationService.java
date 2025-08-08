package com.expense.split.notification;

import com.expense.split.constants.SplitwiseConstants;
import com.expense.split.models.Group;
import com.expense.split.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationService {

  public void notifyGroup(Group group, String message) {
    for (User member : group.getMembers()) {
      log.info(SplitwiseConstants.NOTIFICATION_MSG, member.getName(), message);
    }
  }

  public void notifyUser(User user, String message) {
    log.info(SplitwiseConstants.NOTIFICATION_MSG, user.getName(), message);
  }
}
