export const textAsArrayFormatter = (cell) => {
    return cell.map(elem => {
        return (
            <p key={`elem-${elem}`}>
                {elem}
            </p>
        );
    });
}