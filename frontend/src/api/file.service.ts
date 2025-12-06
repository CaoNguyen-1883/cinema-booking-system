import axiosInstance from './axios';
import type { ApiResponse, FileUploadResponse } from '@/types';

/**
 * File Service
 * Handles file upload operations
 */
export const fileService = {
  /**
   * POST /api/files/upload/image
   * Upload single image
   */
  uploadImage: async (file: File): Promise<FileUploadResponse> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await axiosInstance.post<
      ApiResponse<FileUploadResponse>
    >('/files/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data.data;
  },

  /**
   * POST /api/files/upload/movie-poster
   * Upload movie poster image
   */
  uploadMoviePoster: async (file: File): Promise<FileUploadResponse> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await axiosInstance.post<
      ApiResponse<FileUploadResponse>
    >('/files/upload/movie-poster', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data.data;
  },

  /**
   * POST /api/files/upload/movie-backdrop
   * Upload movie backdrop image
   */
  uploadMovieBackdrop: async (file: File): Promise<FileUploadResponse> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await axiosInstance.post<
      ApiResponse<FileUploadResponse>
    >('/files/upload/movie-backdrop', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data.data;
  },

  /**
   * POST /api/files/upload/images
   * Upload multiple images
   */
  uploadImages: async (files: File[]): Promise<FileUploadResponse[]> => {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));

    const response = await axiosInstance.post<
      ApiResponse<FileUploadResponse[]>
    >('/files/upload/images', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data.data;
  },

  /**
   * DELETE /api/files/delete
   * Delete file by URL
   */
  deleteFile: async (fileUrl: string): Promise<void> => {
    await axiosInstance.delete('/files/delete', {
      params: { fileUrl },
    });
  },
};
