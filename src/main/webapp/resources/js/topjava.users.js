// $(document).ready(function () {
$(function () {
    makeEditable({
            formId: "userEditForm",
            ajaxUrl: "ajax/admin/users/",
            update: function () {
                updateTable();
            },
            datatableApi: $("#datatable").DataTable({
                "paging": false,
                "info": true,
                "columns": [
                    {
                        "data": "name"
                    },
                    {
                        "data": "email"
                    },
                    {
                        "data": "roles"
                    },
                    {
                        "data": "enabled"
                    },
                    {
                        "data": "registered"
                    },
                    {
                        "defaultContent": "Edit",
                        "orderable": false
                    },
                    {
                        "defaultContent": "Delete",
                        "orderable": false
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ]
            })
        }
    );
});

function updateTable() {
    $.get(context.ajaxUrl, updateTableWithData);
}

function setEnabled(id, checkBox) {
    let checkBoxState = checkBox.is(':checked');
    $.ajax({
        url: context.ajaxUrl + id + "/enable",
        type: "POST",
        data: {enabled: checkBoxState}
    }).done(function () {
        checkBoxState ? successNoty("Enabled") : successNoty("Disabled");
        checkBox.closest("tr").attr("data-userEnabled", checkBoxState)
    }).fail(function (jqXHR, textStatus, errorThrown) {
        checkBox.prop("checked", !checkBoxState)
        failNoty(jqXHR);
    });
}
