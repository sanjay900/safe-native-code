package slave.security;

import java.security.*;

/**
 * We don't actually need to limit the permissions of the slave at all, since slaves don't matter.
 */
public class SlavePolicy extends Policy {

    @Override
    public PermissionCollection getPermissions(CodeSource codesource) {
        Permissions p = new Permissions();
        p.add(new AllPermission());
        return p;
    }

}
