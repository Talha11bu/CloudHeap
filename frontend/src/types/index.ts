export interface User {
  id: string;
  displayName: string;
  isOwner: boolean;
}

export interface FileMetadata {
  id: string;
  name: string;
  size: number;
  uploadedBy: string;
  r2Key: string;
  uploadTime: string;
}

export interface SessionState {
  sessionId: string;
  expiresAt: number; 
  users: User[];
  files: FileMetadata[];
}

export interface AuthResponse {
  token: string;
  user: User;
  session: SessionState;
}