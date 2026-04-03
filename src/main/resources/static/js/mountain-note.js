// 消込（打ち消し線）をつけるJavaScript
function toggleStrike(checkbox) {
    const textSpan = checkbox.nextElementSibling;
    if (checkbox.checked) {
        textSpan.style.textDecoration = "line-through";
        textSpan.style.color = "#adb5bd"; // 薄いグレーに
    } else {
        textSpan.style.textDecoration = "none";
        textSpan.style.color = "inherit";
    }
}

function addNoteItemInput() {
    const container = document.getElementById('noteItemsContainer');
    const div = document.createElement('div');
    div.className = 'input-group mb-2';
    div.innerHTML = `
        <span class="input-group-text bg-light border-0"><i class="bi bi-check2-square"></i></span>
        <input type="text" class="form-control border-start-0" name="itemNames" placeholder="アイテム名">
        <button class="btn btn-outline-secondary border-start-0" type="button" onclick="this.parentElement.remove()"><i class="bi bi-x"></i></button>
    `;
    container.appendChild(div);
}
