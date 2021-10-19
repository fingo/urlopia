class StorageMock {
    constructor() {
        this.store = {};
    }

    clear() {
        this.store = {};
    }

    getItem(key) {
        return this.store[key];
    }

    setItem(key, value) {
        this.store[key] = String(value);
    }

    removeItem(key) {
        delete this.store[key];
    }
}

export const mockLocalStorage = () => {
    const storageMock = new StorageMock()
    Object.defineProperty(window, 'localStorage', {
        value: storageMock
    })
    return storageMock
}
