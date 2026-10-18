"use strict";

const http = require("node:http");
const { readFile } = require("node:fs/promises");
const path = require("node:path");

const host = "127.0.0.1";
const port = Number(process.env.PORT || 5173);
const pagePath = path.join(__dirname, "BankingManagement.html");

if (!Number.isInteger(port) || port < 1 || port > 65535) {
    throw new Error(`Invalid PORT value: ${process.env.PORT}`);
}

const server = http.createServer(async (request, response) => {
    let pathname;
    try {
        pathname = new URL(request.url, `http://${host}:${port}`).pathname;
    } catch (error) {
        response.writeHead(400, { "Content-Type": "text/plain; charset=utf-8" });
        response.end("Bad request");
        return;
    }

    if (request.method !== "GET" && request.method !== "HEAD") {
        response.writeHead(405, { Allow: "GET, HEAD", "Content-Type": "text/plain; charset=utf-8" });
        response.end("Method not allowed");
        return;
    }

    if (pathname !== "/" && pathname !== "/index.html" && pathname !== "/BankingManagement.html") {
        response.writeHead(404, { "Content-Type": "text/plain; charset=utf-8" });
        response.end("Not found");
        return;
    }

    try {
        const html = await readFile(pagePath);
        response.writeHead(200, {
            "Content-Type": "text/html; charset=utf-8",
            "Content-Length": html.length,
            "Cache-Control": "no-store",
            "X-Content-Type-Options": "nosniff",
            "Referrer-Policy": "no-referrer"
        });
        response.end(request.method === "HEAD" ? undefined : html);
    } catch (error) {
        console.error("Could not serve the banking dashboard:", error);
        response.writeHead(500, { "Content-Type": "text/plain; charset=utf-8" });
        response.end("Unable to load the banking dashboard");
    }
});

server.on("error", (error) => {
    console.error("Development server failed:", error.message);
    process.exitCode = 1;
});

server.listen(port, host, () => {
    console.log(`Banking dashboard is running at http://${host}:${port}`);
});
