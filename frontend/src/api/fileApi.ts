/**
 * API functions for file operations within a session.
 */
import { API_BASE } from '../config';

/** Gets a pre-signed upload URL from the backend. */
export async function getUploadUrl(
	sessionId: string,
	fileName: string,
	contentType: string,
	token: string
): Promise<{ url: string; fileKey: string }> {
	const params = new URLSearchParams({ fileName, contentType });

	const res = await fetch(
		`${API_BASE}/sessions/${sessionId}/upload-url?${params}`,
		{
			method: 'GET',
			headers: { Authorization: `Bearer ${token}` },
		}
	);

	if (!res.ok) throw new Error(`Failed to get upload URL: ${res.status}`);
	return res.json();
}

/** Uploads a file directly to R2 using a pre-signed URL. */
export async function uploadToR2(
	url: string,
	file: File
): Promise<void> {
	const res = await fetch(url, {
		method: 'PUT',
		body: file,
		headers: { 'Content-Type': file.type || 'application/octet-stream' },
	});

	if (!res.ok) throw new Error(`Failed to upload to R2: ${res.status}`);
}

/** Confirms a completed upload with the backend so it can persist metadata and notify. */
export async function confirmUpload(
	sessionId: string,
	fileName: string,
	fileKey: string,
	fileSize: number,
	token: string
): Promise<void> {
	const res = await fetch(
		`${API_BASE}/sessions/${sessionId}/upload-complete`,
		{
			method: 'POST',
			headers: {
				Authorization: `Bearer ${token}`,
				'Content-Type': 'application/json',
			},
			body: JSON.stringify({ fileName, fileKey, fileSize }),
		}
	);

	if (!res.ok) throw new Error(`Failed to confirm upload: ${res.status}`);
}

/** Gets a pre-signed download URL for a single file. */
export async function getDownloadUrl(
	sessionId: string,
	fileName: string,
	token: string
): Promise<string> {
	const res = await fetch(
		`${API_BASE}/sessions/${sessionId}/files/${encodeURIComponent(fileName)}/download-url`,
		{
			method: 'GET',
			headers: { Authorization: `Bearer ${token}` },
		}
	);

	if (!res.ok) throw new Error(`Server returned ${res.status}`);

	const data = await res.json();
	return data.url;
}

/** Downloads all session files as a ZIP archive. */
export async function downloadZip(
	sessionId: string,
	password: string | undefined,
	token: string
): Promise<Blob> {
	const url = new URL(`${API_BASE}/sessions/${sessionId}/files/zip`);
	if (password) url.searchParams.append('password', password);

	const res = await fetch(url.toString(), {
		method: 'GET',
		headers: { Authorization: `Bearer ${token}` },
	});

	if (!res.ok) throw new Error('Failed to generate zip archive');
	return res.blob();
}

/** Deletes a single file from a session. */
export async function deleteSessionFile(
	sessionId: string,
	fileName: string
): Promise<void> {
	await fetch(
		`${API_BASE}/sessions/${sessionId}/file?fileName=${encodeURIComponent(fileName)}`,
		{ method: 'DELETE' }
	);
}
