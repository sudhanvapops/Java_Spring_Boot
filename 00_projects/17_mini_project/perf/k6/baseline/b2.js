import http from "k6/http"
import { check, fail, sleep } from "k6"


const BASE_URL = "http://localhost:8080"

export const options = {

    scenarios: {
        warmup: {
            executor: "constant-vus",
            exec: "warmup",
            vus: 30,
            duration: "5s",
        },
        author_search: {
            executor: "constant-vus",
            exec: "searchAuthor",
            vus: 50,
            duration: "30s",
            // startTime: "15s"
        },
        member_search: {
            executor: "constant-vus",
            exec: "memberRecords",
            vus: 50,
            duration: "30s",
            // startTime: "45s"
        },
        member_transaction_records_search: {
            executor: "constant-vus",
            exec: "memberTransactionRecords",
            vus: 50,
            duration: "30s",
            // startTime: "75s"
        }
    },
    summaryTrendStats: ["avg", "med", "p(90)", "p(95)", "p(99)", "max"],
    thresholds: {
        "http_req_failed{name:author_search}": ["rate<0.01"],
        "http_req_failed{name:member_records}": ["rate<0.01"],
        "http_req_failed{name:member_transaction_records}": ["rate<0.01"],
        "http_req_duration{name:author_search}": ["p(95)<2000"],
        "http_req_duration{name:member_records}": ["p(95)<2000"],
        "http_req_duration{name:member_transaction_records}": ["p(95)<2000"],
    },
}


export function setup() {

    const res = http.post(
        `${BASE_URL}/api/auth/login`,
        JSON.stringify({ email: "admin@library.local", password: "Admin@12345" }),
        { headers: { "Content-Type": "application/json" } }
    )

    if (res.status !== 200) fail(`login failed: ${res.status} ${res.body}`)

    return { token: res.json("data.accessToken") }

}


function auth(data) {
    return { Authorization: `Bearer ${data.token}` };
}

export function warmup(data) {
    http.get(`${BASE_URL}/api/book/author?author=author%201`, { headers: auth(data), tags: { name: "warmup" } });
    http.get(`${BASE_URL}/api/borrowrecord/member-id/1`, { headers: auth(data), tags: { name: "warmup" } });
    http.get(`${BASE_URL}/api/borrow-transactions/member-id/1`, { headers: auth(data), tags: { name: "warmup" } });
    sleep(0.5);
}

export function searchAuthor(data) {
    const n = Math.floor(Math.random() * 5000);
    const res = http.get(
        `${BASE_URL}/api/book/author?author=${encodeURIComponent("author " + n)}`,
        { headers: auth(data), tags: { name: "author_search" } }
    );
    check(res, { "author: 200": (r) => r.status === 200 });
    sleep(0.5);
}

export function memberRecords(data) {
    const id = 1 + Math.floor(Math.random() * 10000);   // seeded members: 1..10000
    const res = http.get(
        `${BASE_URL}/api/borrowrecord/member-id/${id}`,
        { headers: auth(data), tags: { name: "member_records" } }
    );
    check(res, {
        "member records: 200": (r) => r.status === 200,
        "member records: 10 rows": (r) => r.json("data").length === 10,
    });
    sleep(0.5);
}

export function memberTransactionRecords(data) {
    const id = 1 + Math.floor(Math.random() * 10000);   // seeded members: 1..10000
    const res = http.get(
        `${BASE_URL}/api/borrow-transactions/member-id/${id}`,
        { headers: auth(data), tags: { name: "member_transaction_records" } }
    );
    check(res, {
        "member transaction records: 200": (r) => r.status === 200, "transactions: 5 rows": (r) => r.json("data").length === 5,
    });
    sleep(0.5);
}