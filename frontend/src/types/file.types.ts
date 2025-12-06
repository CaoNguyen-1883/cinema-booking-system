// File Upload Types

export interface FileUploadResponse {
  fileName: string;
  originalName: string;
  url: string;
  contentType: string;
  size: number;
}
