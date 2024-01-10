import {Pagination} from "react-bootstrap";

import styles from "./PaginationHelper.module.scss";

export const getPaginationForPage = ({page, onClick}) => {
    const {totalPages} = page;

    if (totalPages > 1) {
        const {pageNumber} = page.pageable;

        let items = [];
        for (let i = 0; i < totalPages; i++) {
            items.push(
                <Pagination.Item
                    key={i}
                    className={styles.item}
                    active={i === pageNumber}
                    onClick={() => onClick(i)}
                >
                    {i + 1}
                </Pagination.Item>
            )
        }

        return (
            <Pagination className={styles.pagination}>
                <Pagination.Prev
                    onClick={() => {
                        if (pageNumber > 0) {
                            onClick(pageNumber - 1)
                        }
                    }}
                />
                {items}
                <Pagination.Next
                    onClick={() => {
                        if (pageNumber < totalPages - 1) {
                            onClick(pageNumber + 1)
                        }
                    }}
                />
            </Pagination>
        )
    } else {
        return null;
    }
}