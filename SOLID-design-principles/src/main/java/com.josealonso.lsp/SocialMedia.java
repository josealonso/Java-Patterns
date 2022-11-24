package com.josealonso.solid.lsp;

public abstract class SocialMedia {

    // @support WhatsApp, Facebook, Instagram
    public abstract void chatWithFriends();

    // @support Facebook, Instagram
    public abstract void publishPost(Object post);

    // @support WhatsApp, Facebook
    public abstract void groupVideoCall();
}

