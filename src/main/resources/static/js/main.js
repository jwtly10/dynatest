let currentConfigEditor;
let mainConfigErrorAlert;
let mainConfigSuccessAlert;

// ***********************************************************************************
// ON LOAD
// ***********************************************************************************
document.addEventListener('DOMContentLoaded', (event) => {
    mainConfigErrorAlert = document.getElementById("updateTestSuiteError")
    mainConfigSuccessAlert = document.getElementById("updateTestSuiteSuccess")

    currentConfigEditor = CodeMirror.fromTextArea(document.getElementById('testSuiteConfig'), {
        mode: "application/json",
        // theme: "monokai",
        lineNumbers: true,
        gutters: ["CodeMirror-lint-markers"],
        lint: true,
    });

    // Init test suite nav links
    const navLinks = document.querySelectorAll('.nav-link.collapsed');
    navLinks.forEach(link => {
        link.addEventListener('click', async function (e) {
            showMainContent()
            hideAlerts()
            e.preventDefault();
            const testSuiteId = this.getAttribute('data-id');
            await fetchTestSuiteDetails(testSuiteId);
        });
    });


    // Init buttons
    document.getElementById('confirmDeleteButton').addEventListener('click', function (e) {
        e.preventDefault()
        const id = document.getElementById("testSuiteId").value
        deleteTestSuite(id)
        deleteSideBarTestSuiteName(id)
        showWelcomePage()

        // Close the modal
        $('#deleteConfirmModal').modal('hide');
    });
    document.getElementById('run').addEventListener('click', async function (e) {
        e.preventDefault()

        const id = document.getElementById("testSuiteId").value
        console.log(`Running test suite with id ${id}`)
        const logContainer = document.getElementById('outputLogs');
        logContainer.innerHTML = '';

        document.getElementById('spinner').style.display = 'inline-block';
        const lastResult = document.getElementById("lastResult")
        lastResult.className = ""
        await runTestSuite(id)
        document.getElementById('spinner').style.display = 'none';
    })

    const newTestSuiteBtn = document.getElementById("newTestSuite")
    newTestSuiteBtn.addEventListener('click', async function (e) {
        // Init an example test suite
        const exampleJson = exampleTestJson // See end of file

        const newData = {
            name: "New Test Suite",
            configuration: JSON.stringify(exampleJson)
        }
        const res = await postNewTestSuite(newData.name, newData.configuration)
        newData.id = res.id

        const sidebarList = document.getElementById('accordionSidebar');

        const newListItem = document.createElement('li');
        newListItem.className = 'nav-item';

        const newLink = document.createElement('a');
        newLink.className = 'nav-link collapsed d-flex justify-content-between align-items-center';
        newLink.href = '#';
        newLink.setAttribute('data-id', newData.id);
        const newSpan = document.createElement('span');
        newSpan.innerText = 'New Test Suite';
        const newIcon = document.createElement('i');
        newIcon.className = "fas fa-chevron-right chevron-icon"

        newLink.appendChild(newSpan);
        newLink.appendChild(newIcon);
        newListItem.appendChild(newLink);
        sidebarList.appendChild(newListItem);

        newListItem.addEventListener('click', async function (e) {
            showMainContent()
            hideAlerts()
            e.preventDefault();
            await fetchTestSuiteDetails(newData.id);
        });

        await fetchTestSuiteDetails(newData.id)
        showMainContent()
    })

    const updateTestSuiteBtn = document.getElementById("updateTestSuite")
    updateTestSuiteBtn.addEventListener('click', async function (e) {
        e.preventDefault()

        const id = document.getElementById("testSuiteId").value
        const name = document.getElementById("testSuiteName").value
        const config = currentConfigEditor.getValue()

        await updateTestSuiteDetails(id, name, config)
    })
});

// ***********************************************************************************
// API CLIENT METHODS
// ***********************************************************************************
async function postNewTestSuite(name, testSuiteJson) {
    hideAlerts()
    try {
        const response = await fetch('/test-suites/new', {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                    name: name,
                    configuration: testSuiteJson
                }
            )
        })
        if (response.status !== 200) {
            const resBody = await response.text()
            console.error("Found error: " + response.status + " " + resBody)
            return
        }
        return await response.json()
    } catch (e) {
        console.error("Cant post new test suite: ", e)
    }
}

async function deleteTestSuite(id) {
    hideAlerts()
    try {
        await fetch(`/test-suites/delete/${id}`, {method: "DELETE"})
    } catch (e) {
        console.error("Error deleting suite:", e)
        setAlert(e.message, "error-main")
    }
}

async function fetchTestSuiteDetails(id) {
    hideAlerts()
    try {
        const response = await fetch(`/test-suites/${id}`, {
            method: "GET",
        })
        if (response.status !== 200) {
            const resBody = await response.text()
            console.error("Found error: " + response.status + " " + resBody)
            return
        }
        const data = await response.json()
        console.log("Found test suite data: " + data)
        updateMainContent(data)
    } catch (e) {
        console.error('Error fetching test suite details:', e)
    }
}

async function runTestSuite(id) {
    hideAlerts()
    try {
        const response = await fetch(`execute/run/${id}`)
        if (response.status !== 200) {
            const resBody = await response.text()
            console.error("Found error: " + response.status + " " + resBody)
            renderErrorInOutputLog(resBody)
            return
        }
        const data = await response.json()
        console.log(data)
        updateMainContent(data)
    } catch (e) {
        console.error("Error running test suite: ", e)
    }
}

async function updateTestSuiteDetails(id, name, config) {
    hideAlerts()
    try {
        const response = await fetch(`test-suites/update/${id}`, {
            method: "PUT",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(
                {
                    name: name,
                    configuration: config
                }
            )
        })
        if (response.status !== 200) {
            console.error("Status code is not 200 ", response.statusText)
            const resBody = await response.text()
            setAlert(resBody, "error-main")
            return
        }
        console.log(response)
        setAlert("Successfully updated test suite", "success-main")
        updateSidebarTestSuiteName(id, name)
    } catch (e) {
        console.error("Cant update test suite: ", e)
        setAlert(e.message, "error-main")
    }
}


// ***********************************************************************************
// UTILS
// ***********************************************************************************
function formatDuration(ms) {
    return `${ms} ms`
}

function formatDate(date) {
    if (date) {
        return new Date(date).toLocaleString()
    }
    return ""
}

function formatOutputReport(report) {
    return report.replace("\n", "<br>")
}

// ***********************************************************************************
// HELPERS
// ***********************************************************************************
function updateMainContent(data) {
    const testSuiteName = document.getElementById("testSuiteName")
    const testSuiteId = document.getElementById("testSuiteId")

    const metaRuns = document.getElementById("metaRuns")
    const metaPassCount = document.getElementById("metaPassCount")
    const metaFailCount = document.getElementById("metaFailCount")
    const metaLastOutcome = document.getElementById("metaLastOutcome")
    const metaLastFinished = document.getElementById("metaLastFinished")
    const metaCreated = document.getElementById("metaCreated")
    const metaUpdated = document.getElementById("metaUpdated")

    const lastResult = document.getElementById("lastResult")

    try {
        currentConfigEditor.setValue(JSON.stringify(JSON.parse(data.entity.configuration), null, 2))
    } catch (e) {
        console.error("Error formating JSON: " + e)
        currentConfigEditor.setValue(data.entity.configuration)
    }
    testSuiteName.value = data.entity.name
    testSuiteId.value = data.entity.id

    metaRuns.textContent = data.metaData.runs
    metaPassCount.textContent = data.metaData.passCount
    metaFailCount.textContent = data.metaData.failCount
    metaLastOutcome.textContent = data.metaData.lastOutcome
    switch (data.metaData.lastOutcome) {
        case "SUCCESS":
            lastResult.className = "fa fa-check text-success"
            break;
        case "FAIL":
            lastResult.className = "fa fa-times text-danger"
            break;
        default :
            lastResult.className = ""
            break;
    }

    metaLastFinished.textContent = formatDate(data.metaData.lastFinishedRunAt)
    metaCreated.textContent = formatDate(data.entity.createdAt)
    metaUpdated.textContent = formatDate(data.entity.updatedAt)

    displayHistoryRunLog(data.runLogs)
    if (data.metaData.lastRunLog !== "") {
        try {
            const logs = JSON.parse(data.metaData.lastRunLog)
            renderLogs(logs)
        } catch (e) {
            const logContainer = document.getElementById('outputLogs');
            logContainer.innerHTML = '';
            console.error("Failed to parse JSON logs from output logs")
        }
    } else {
        const logContainer = document.getElementById('outputLogs');
        logContainer.innerHTML = '';
    }
}

function renderErrorInOutputLog(error) {
    const logContainer = document.getElementById('outputLogs');
    logContainer.innerHTML = '';
    const logElement = document.createElement('div');
    logElement.className = 'log-error';
    logElement.innerHTML = `Sorry, an unexpected server error occured:<br><br>${error}`
    logContainer.appendChild(logElement);
}

function renderLogs(logs) {
    const logContainer = document.getElementById('outputLogs');
    logContainer.innerHTML = '';

    logs.forEach(log => {
        const logElement = document.createElement('div');
        if (log.type === 'INFO') {
            logElement.className = 'log-info';
            logElement.innerHTML = `<b>${log.type}:</b> ${log.message}`;
        } else if (log.type === 'ERROR') {
            logElement.className = 'log-error';
            logElement.innerHTML = `<b>${log.type}:</b> ${log.message}`;
        } else if (log.type === 'DEBUG') {
            logElement.className = 'log-debug';
            logElement.innerHTML = `<b>${log.type}:</b> ${log.message}`;
        } else if (log.type === 'WARN') {
            logElement.className = 'log-warn';
            logElement.innerHTML = `<b>${log.type}:</b> ${log.message}`;
        } else if (log.type === 'STEP_PASS') {
            logElement.className = 'log-pass';
            logElement.innerHTML = `
            <br>
            ======================================================= <br>
            ===================== ${log.message} ===================== <br>
            ======================================================= <br>
            <br>
            `
        } else if (log.type === 'TEST_PASS') {
            logElement.className = 'log-pass';
            logElement.innerHTML = `
            <br>
            ${log.message}`;
        } else if (log.type === 'FAIL') {
            logElement.className = 'log-fail';
            logElement.innerHTML = `
            ======================================================= <br>
            ================== TEST SUITE FAILED ================== <br>
            ======================================================= <br>
            ${log.message}`;
        } else if (log.type === 'OUTPUT_REPORT') {
            logElement.className = 'log-pass';
            logElement.textContent = log.message;
        } else if (log.type === 'JSON') {
            logElement.className = 'log-json';
            try {
                logElement.textContent = JSON.stringify(JSON.parse(log.message), null, 2)
            } catch (e) {
                console.error("Failed to parse JSON in logs: ", e)
                logElement.textContent = log.message;
            }
        }

        logElement.className = `${logElement.className} mb-1`
        logContainer.appendChild(logElement);
        logContainer.scrollTop = logContainer.scrollHeight;
    });
}

function displayHistoryRunLog(logs) {
    const container = document.getElementById('runLogsContainer');
    container.innerHTML = '';

    logs.forEach((log, index) => {
        const logRow = document.createElement('tr');

        const status = document.createElement("i");
        switch (log.status) {
            case "SUCCESS":
                status.className = "fa fa-check text-success";
                break;
            case "FAIL":
                status.className = "fa fa-times text-danger";
                break;
            default:
                return;
        }

        logRow.innerHTML = `
            <td>${logs.length - index}</td>
            <td></td>
            <td>${new Date(log.startTime).toLocaleString()}</td>
            <td>${new Date(log.endTime).toLocaleString()}</td>
            <td>${formatDuration(log.duration)}</td>
        `;

        logRow.cells[1].appendChild(status);

        container.appendChild(logRow);
    });
}

function updateSidebarTestSuiteName(id, newName) {
    const listItem = document.querySelector(`a[data-id='${id}']`);
    if (listItem) {
        const spanElement = listItem.querySelector('span');
        if (spanElement) {
            spanElement.textContent = newName;
        }
    }
}

function deleteSideBarTestSuiteName(id) {
    const listItem = document.querySelector(`a[data-id='${id}']`);
    if (listItem) {
        const parentLi = listItem.closest('li');
        if (parentLi) {
            parentLi.remove();
        }
    }
}

function hideAlerts() {
    mainConfigErrorAlert.style.display = "none"
    mainConfigErrorAlert.innerHTML = ""

    mainConfigSuccessAlert.style.display = "none"
    mainConfigSuccessAlert.innerHTML = ""
}

function showWelcomePage() {
    document.getElementById('placeholder-main-content').style.display = 'block';
    document.getElementById('main-content').style.display = 'none';
}

function showMainContent() {
    document.getElementById('placeholder-main-content').style.display = 'none';
    document.getElementById('main-content').style.display = 'block';
}

function setAlert(message, type) {
    switch (type) {
        case "success-main":
            mainConfigSuccessAlert.style.display = "block"
            mainConfigSuccessAlert.innerHTML = message
            break;
        case "error-main":
            mainConfigErrorAlert.style.display = "block"
            mainConfigErrorAlert.innerHTML = message
            break;
        default:
            console.error("This alert type doesnt exist: " + type)
            break;
    }
}

const exampleTestJson = {
    "tests": [
        {
            "name": "Example server create profile and survey",
            "vars": {
                "BASE_URL": "http://localhost:8080/demo",
                "X-API-KEY": "71b80106-be79-41a9-bc79-ed0b1ecfe0b7",
                "EMAIL": "${randomEmail(demo)}"
            },
            "description": "",
            "steps": [
                {
                    "stepName": "Create profile",
                    "request": {
                        "method": "POST",
                        "url": "${BASE_URL}/create-member",
                        "headers": {
                            "Content-Type": "application/json",
                            "Accept": "application/json",
                            "X-API-KEY": "${X-API-KEY}"
                        },
                        "body": {
                            "firstName": "Test",
                            "lastName": "Test",
                            "email": "${EMAIL}",
                            "dob": "2000-03-25",
                            "phone": "077041357733"
                        },
                        "storeValues": {
                            "profileID": "body.userId"
                        }
                    },
                    "expectedResponse": {
                        "statusCode": 200,
                        "validationSchema": {
                            "type": "object",
                            "properties": {
                                "userId": {
                                    "type": "number"
                                }
                            },
                            "required": [
                                "userId"
                            ]
                        }
                    }
                },
                {
                    "stepName": "Check profile",
                    "request": {
                        "method": "GET",
                        "url": "${BASE_URL}/get-member",
                        "headers": {
                            "Content-Type": "application/json",
                            "Accept": "application/json",
                            "X-API-KEY": "${X-API-KEY}"
                        },
                        "queryParams": {
                            "id": "${profileID}"
                        }
                    },
                    "expectedResponse": {
                        "statusCode": 200,
                        "validationSchema": {
                            "type": "object",
                            "properties": {
                                "id": {
                                    "type": "number"
                                },
                                "firstName": {
                                    "type": "string"
                                },
                                "lastName": {
                                    "type": "string"
                                },
                                "email": {
                                    "type": "string"
                                },
                                "phone": {
                                    "type": "string"
                                },
                                "dob": {
                                    "type": "string"
                                }
                            },
                            "required": [
                                "id",
                                "firstName",
                                "lastName",
                                "email",
                                "phone",
                                "dob"
                            ]
                        }
                    }
                },
                {
                    "stepName": "Submit Survey",
                    "request": {
                        "method": "POST",
                        "url": "${BASE_URL}/create-survey",
                        "headers": {
                            "Content-Type": "application/json",
                            "Accept": "application/json",
                            "X-API-KEY": "${X-API-KEY}"
                        },
                        "body": {
                            "profileId": "${profileID}",
                            "surveyData": "Example survey answer"
                        }
                    },
                    "expectedResponse": {
                        "statusCode": 200,
                        "validationSchema": {
                            "type": "object",
                            "properties": {
                                "surveyId": {
                                    "type": "number"
                                }
                            },
                            "required": [
                                "surveyId"
                            ]
                        }
                    }
                }
            ]
        }
    ]
}