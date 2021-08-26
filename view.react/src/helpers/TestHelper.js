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

export const mockSessionStorage = () => {
    const storageMock = new StorageMock()
    Object.defineProperty(window, 'sessionStorage', {
        value: storageMock
    })
    return storageMock
}
