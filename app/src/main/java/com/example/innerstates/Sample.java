package com.example.innerstates;

public class Sample {
    public static final int READY = 0, IG_OPENED = 1, POPUP = 2, WAIT_FOR_NEXT_POPUP = 3, ON_IG=4;

    private int sampleTime = 0;
    private long sampleStartTime;
    private long sampleEndTime;
    private boolean isIgOpenFirst;
    private int status;

    public Sample() {
        this.isIgOpenFirst = false;
        this.status = READY;
    }
    public boolean getIsIgOpenFirst() { return this.isIgOpenFirst;}
    public void setIsIgOpenFirst(boolean ele) { this.isIgOpenFirst = ele;}
    public int getStatus() { return this.status; }
    public void setStatus(int status) {
        if(status == WAIT_FOR_NEXT_POPUP) {
            MainService.startWaitNextNotificationTime = MyUtil.getCurrentTime1000();
        }
        this.status = status;
    }
    public String getReadableStatus() {
        switch (this.status) {
            case READY: return "READY";
            case IG_OPENED: return "IG_OPENED";
            case ON_IG: return "ON_IG";
            case POPUP: return "POPUP";
            case WAIT_FOR_NEXT_POPUP: return "WAIT_FOR_NEXT_POPUP";

        }
        return "NA";
    }
}
