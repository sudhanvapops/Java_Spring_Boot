import http from "k6/http"
import { check, sleep } from 'k6';

export const options = {
    vus: 10,
    duration: "2s",
    // What are the mesuremnts to display at the end 
    summaryTrendStats: ["avg", "med", "p(90)", "p(95)", "p(99)", "max"],
    threshold: {
        http_req_failed:['rate<0.000001'],
        http_req_duration: ['p(99)<0001']
    }
}

export default () => {
    const res = http.get("http://localhost:3000/hello")

    // Validate the response status code is 200
    check(res, {
        'is status 200': (r) => r.status === 200,
    });
}