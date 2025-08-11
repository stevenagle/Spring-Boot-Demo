// src/utils/cookies.ts
export function setCookie(name: string, value: string, days: number) {
  const expires = new Date(Date.now() + days * 864e5).toUTCString();
  document.cookie = `${encodeURIComponent(name)}=${encodeURIComponent(value)}; expires=${expires}; path=/`;
}

export function getCookie(name: string) {
  return document.cookie
    .split('; ')
    .find(row => row.startsWith(`${encodeURIComponent(name)}=`))
    ?.split('=')[1];
}

export function deleteCookie(name: string) {
  document.cookie = `${encodeURIComponent(name)}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/`;
}
