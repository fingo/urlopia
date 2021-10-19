export const formattedDate = date => {
    const fullYear = date.getFullYear()
    const month = padValue(date.getMonth() + 1)
    const day = padValue(date.getDate())
    return `${fullYear}-${month}-${day}`
}

export const formattedTime = date => {
    const hours = padValue(date.getHours())
    const minutes = padValue(date.getMinutes())
    return `${hours}:${minutes}`
}

const padValue = val => {
    return val.toString().padStart(2, "0")
}