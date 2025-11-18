export const triggerDownload = (blob: Blob, filename: string) => {
  const url = window.URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  document.body.appendChild(anchor);
  anchor.click();
  document.body.removeChild(anchor);
  window.URL.revokeObjectURL(url);
};

export const resolveFilename = (contentDisposition?: string, fallback?: string) => {
  if (!contentDisposition) {
    return fallback;
  }
  const matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(contentDisposition);
  if (matches != null && matches[1]) {
    return decodeURIComponent(matches[1].replace(/['"]/g, ''));
  }
  return fallback;
};
