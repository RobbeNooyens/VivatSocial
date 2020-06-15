package net.vivatcreative.social.api;

import net.vivatcreative.core.permissions.VivatPermission;

public enum SocialPermission implements VivatPermission {
    CREATE_PARTY("party.create");


    private final String node;

    SocialPermission(String node){
        this.node = node;
    }

    @Override
    public String getNode() {
        return node;
    }
}
