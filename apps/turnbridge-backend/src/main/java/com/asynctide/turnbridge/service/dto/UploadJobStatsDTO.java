package com.asynctide.turnbridge.service.dto;

public class UploadJobStatsDTO {
 private long total;
 private long ok;
 private long error;
 private long queued;

 public UploadJobStatsDTO() {}
 public UploadJobStatsDTO(long total, long ok, long error, long queued) {
     this.total = total; this.ok = ok; this.error = error; this.queued = queued;
 }
 public long getTotal() { return total; }
 public void setTotal(long total) { this.total = total; }
 public long getOk() { return ok; }
 public void setOk(long ok) { this.ok = ok; }
 public long getError() { return error; }
 public void setError(long error) { this.error = error; }
 public long getQueued() { return queued; }
 public void setQueued(long queued) { this.queued = queued; }
}
