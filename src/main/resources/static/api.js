let csrfToken;
let alerPlate = document.getElementById('successAlert');
let timerLine = document.querySelector('.timer-line');
let alertMsg = document.getElementById('alertMsg');
const tableBody = document.getElementById('users-table-body');


getUsersList();
getAuthorizedUser();

window.onload = function(){
    fetch('/api/admin/sendCSRF')
    .then(response => response.text())
    .then(token => {
        csrfToken = token;
        console.log(`Получили CSRF-токен: ${csrfToken}`);
    })
    .catch(error => {
        console.error('Не удалось получить CSRF-токен:', error);
    });
}

function showSuccess(msg) {
    timerLine.style.width = '0%';
    
    alerPlate.classList.remove('hidden')
    alerPlate.classList.add('active');
    alertMsg.innerHTML = msg;
    
    setTimeout(() => {
        timerLine.style.width = '100%';
    }, 200);
}

function hideSuccess() {
    timerLine.style.width = '0%';
    
    setTimeout(() => {
        alerPlate.classList.remove('active');
        alerPlate.classList.add('hidden');
    }, 5000);
}

function anim(text) {
    showSuccess(text);
    setTimeout(hideSuccess, 1000);
}

function refreshData() {
    getUsersList();
    getAuthorizedUser();
}

function getUsersList() {
    fetch('/api/admin/users')
        .then(response => response.json())
        .then(users => {
            let rowsHtml = '';
            tableBody.innerHTML = '';

            users.forEach(user => {
                const roleNames = user.roles
                    .map(role => role.roleName.replace('ROLE_', ''))
                    .join(' ');
                rowsHtml +=
                `
                    <tr class="alert-secondary">
                        <td class="py-2 pl-2">${user.id}</td>
                        <td>${user.name}</td>
                        <td>${user.lastname}</td>
                        <td>${user.age}</td>
                        <td>${user.email}</td>
                        <td>${roleNames}</td>
                        <td class="py-2">
                            <button
                            class="btn btn-info open-edit-modal"
                            id="editBtn"
                            data-toggle="modal"
                            data-target="#editUserModal"
                            data-id="${user.id}"
                            data-name="${user.name}"
                            data-lastname="${user.lastname}"
                            data-age="${user.age}"
                            data-email="${user.email}"
                            data-roles="${roleNames}">Edit</button>
                        </td>
                        <td class="py-2">
                            <button
                            class="btn btn-danger open-delete-modal"
                            id="deleteBtn"
                            data-toggle="modal"
                            data-target="#deleteUserModal"
                            data-id="${user.id}"
                            data-name="${user.name}"
                            data-lastname="${user.lastname}"
                            data-age="${user.age}"
                            data-email="${user.email}"
                            data-roles="${roleNames}">Delete</button>
                        </td>
                    </tr>
                `;
            });

            tableBody.innerHTML = rowsHtml;
        })
        .catch(error => {
            console.error('Error:', error);
        });
};

function getAuthorizedUser() {
    fetch('api/admin/authorizedUser')
    .then(response => response.json())
    .then(user => {
        const headerEmail = document.getElementById('userDataEmail');
        const headerRoles = document.getElementById('userDataRoles');
        const authorizedUserData = document.getElementById('authorizedUserData');

        const roleNames = user.roles
            .map(role => role.roleName.replace('ROLE_', ''))
            .join(' ');

        headerEmail.textContent = user.email;
        headerRoles.textContent = roleNames;

        rowUserDataHtml =
        `
        <tr th:block th:fragment="user-info(userToShow)" class="border-top alert-secondary">
            <td class="py-2 pl-2">${user.id}</td>
            <td>${user.name}</td>
            <td>${user.lastname}</td>
            <td>${user.age}</td>
            <td>${user.email}</td>
            <td class="py-2">${roleNames}</td>
        </tr>
        `

        authorizedUserData.innerHTML = rowUserDataHtml


    })
    .catch(error => {
        console.error('Error:', error);
    });
}

document.getElementById('addNewUser').addEventListener('submit', (event) => {
    event.preventDefault()

    // Собираем данные из формы
    const user = {
        name: document.getElementById('nameToAdd').value,
        lastname: document.getElementById('lastnameToAdd').value,
        age: document.getElementById('ageToAdd').value,
        email: document.getElementById('emailToAdd').value,
        password: document.getElementById('passwordToAdd').value,
        roles: Array.from(document.querySelectorAll('input[name="roleToAdd"]:checked'))
            .map(checkbox => checkbox.value)
    }

    // Отправляем POST-запрос
    fetch('/api/admin/user/new', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken // Используем сохраненный токен
        },
        body: JSON.stringify(user)
    })
        .then(response => {
            if (response.ok) {

                // Получаем список пользователей
                anim('Пользователь успешно добавлен!');
                refreshData();

                let usersListBtn = document.getElementById('showUsersListBtn')
                let addFormBtn = document.getElementById('showAddUserFormBtn')
                const usersList = document.getElementById('usersList');
                const addUserForm = document.getElementById('addUserForm');

                usersListBtn.classList.add('active')
                usersListBtn.classList.remove('text-black')
                usersListBtn.classList.add('text-primary')

                addFormBtn.classList.remove('active')
                addFormBtn.classList.add('text-black')
                addFormBtn.classList.remove('text-primary')

                usersList.style.display = 'block';
                addUserForm.style.display = 'none';

                // Очищаем форму для нового ввода данных
                document.getElementById('nameToAdd').value = '';
                document.getElementById('lastnameToAdd').value = '';
                document.getElementById('ageToAdd').value = '';
                document.getElementById('emailToAdd').value = '';
                document.getElementById('passwordToAdd').value = '';
                document.querySelectorAll('input[name="roleToAdd"]').forEach(role => role.checked = false);
            } else {
                anim('Ошибка добавления пользователя')
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
});

document.getElementById('editUserForm').addEventListener('submit', (event) => {
    event.preventDefault()
 
    // Собираем данные из формы
    const user = {
        id: document.getElementById('editUserId').value,
        name: document.getElementById('editName').value,
        lastname: document.getElementById('editLastname').value,
        age: document.getElementById('editAge').value,
        email: document.getElementById('editEmail').value,
        password: document.getElementById('editPassword').value,
        roles: Array.from(document.querySelectorAll('input[name="roleToEdit"]:checked'))
            .map(checkbox => checkbox.value)
    }

    // Отправляем POST-запрос
    fetch('/api/admin/user/edit', {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken // Используем сохраненный токен
        },
        body: JSON.stringify(user)
    })
        .then(response => {
            if (response.ok) {

                // Получаем список пользователей
                anim('Пользователь успешно обновлен!');
                getUsersList();
                getAuthorizedUser();

                document.getElementById('closeEdit').click()

            } else {
                anim('Ошибка удаления пользователя!');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
});

document.getElementById('deleteUserForm').addEventListener('submit', (event) => {
    event.preventDefault()
    console.log('удалили)')

    const id = document.getElementById('deleteUserId').value;

    // Отправляем DELETE-запрос
    fetch(`/api/admin/user/delete/` + id, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken 
        }
    })
        .then(response => {
            if (response.ok) {

                anim('Пользователь успешно удален!');
                getUsersList();
                getAuthorizedUser();

                document.getElementById('closeDelete').click()
            } else {
                anim('Ошибка удаления пользователя!');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
});