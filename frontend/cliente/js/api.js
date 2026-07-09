const apiBaseUrls = [
    'http://localhost:8080/api',
    'https://gestfy-backend.onrender.com/api'
];

const originalFetch = window.fetch.bind(window);
window.fetch = async (input, init = {}) => {
    const url = input instanceof Request ? input.url : input;
    const token = localStorage.getItem('gestfyToken');

    let headers = new Headers(init.headers || (input instanceof Request ? input.headers : undefined));

    if (typeof url === 'string' && apiBaseUrls.some(base => url.startsWith(base)) && token) {
        headers.set('Authorization', `Bearer ${token}`);
    }

    const requestInit = { ...init, headers };
    const request = input instanceof Request ? new Request(input, requestInit) : new Request(url, requestInit);

    return originalFetch(request);
};
