import http from "k6/http";

import { check, fail, sleep } from "k6"

const BASE_URL = "http://localhost:8080"

// OPTIONS: how much load, how long, what counts as "bad"
export const options = {
    // virtual users running the default function in parallel loops
    vus: 10,
    duration: "60s",
    summaryTrendStats: ["avg", "med", "p(90)", "p(95)", "p(99)", "max"],
    thresholds: {
        // pass/fail rules; 
        // k6 exits non-zero if broken
        http_req_failed: ["rate<0.01"], // how much are allowed to fail
        http_req_duration: ["p(95)<2000"], // how long a request should take
    },
};
 

// SETUP: runs ONCE before the load. Whatever it returns goes to every VU.
export function setup() {
    const res = http.post(
        `${BASE_URL}/api/auth/login`,
        JSON.stringify({ email: "admin@library.local", password: "Admin@12345" }),
        { headers: { "Content-Type": "application/json" } }
    );
    if (res.status !== 200) fail(`login failed: ${res.status} ${res.body}`);
    return { token: res.json("data.accessToken") };
}


// DEFAULT FUNCTION: one "iteration". 
// Each VU calls it again and again.
export default (data) => {
    // authors are 'author 0'..'author 4999'
    const n = Math.floor(Math.random() * 5000);       
    const res = http.get(
        `${BASE_URL}/api/book/author?author=${encodeURIComponent("author " + n)}`,
        { headers: { Authorization: `Bearer ${data.token}` } }
    );

    // checks = assertions that are COUNTED, not fatal (thresholds decide pass/fail)
    check(res, { "status is 200": (r) => r.status === 200 });

    sleep(0.5);   // think time: a real user pauses between requests
}