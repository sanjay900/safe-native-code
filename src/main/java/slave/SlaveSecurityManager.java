package slave;

import java.security.Permission;

public class SlaveSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
    }
}
