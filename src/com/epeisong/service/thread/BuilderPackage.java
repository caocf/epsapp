/**
 * 
 * 
 */
package com.epeisong.service.thread;

import com.google.protobuf.GeneratedMessage.Builder;

/**
 * @author cngaohk
 * @since Sep 24, 2014
 */
public class BuilderPackage {
    @SuppressWarnings("rawtypes")
    private Builder msg;
    private int command;
    private int sequence;

    @SuppressWarnings("rawtypes")
    public BuilderPackage(Builder msg, int command, int sequence) {
        this.msg = msg;
        this.command = command;
        this.sequence = sequence;
    }

    /**
     * @return the command
     */
    public int getCommand() {
        return command;
    }

    /**
     * @return the msg
     */
    @SuppressWarnings("rawtypes")
    public Builder getMsg() {
        return msg;
    }

    /**
     * @return the sequence
     */
    public int getSequence() {
        return sequence;
    }

}
