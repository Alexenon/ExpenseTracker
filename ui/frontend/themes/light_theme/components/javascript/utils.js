/**
 * Retrieves a parameter from a formatted string using a formatter pattern.
 *
 * @param {string} string - The input string.
 * @param {string} pattern - The formatter pattern.
 * @param {number} paramIndex - The index of the parameter to retrieve.
 * @returns {string|null} The parameter value or null if not found.
 */
export function getParamFormatter(string, pattern, paramIndex) {
    const params = pattern.match(/\w+/g);
    const escapeRegex = (string) => string.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
    const formatterParts = pattern.split(/\{[^}]+\}/g);
    const values = string.split(new RegExp(formatterParts.map(escapeRegex).join("(.+)")));

    return values?.[paramIndex]?.trim();
}

export function getElementByXPath(xpath) {
    return document.evaluate(
        xpath,
        document,
        null,
        XPathResult.FIRST_ORDERED_NODE_TYPE,
        null
    ).singleNodeValue;
}

export async function getJsonData(url) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Failed to fetch data');
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching JSON:', error);
        return null;
    }
}
