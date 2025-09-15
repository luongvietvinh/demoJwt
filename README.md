Sub Yu_Gothic()
    Dim ws As Worksheet
    Dim rng As Range
    Dim sh As Shape
    Dim cell As Range

    For Each ws In Application.ActiveWorkbook.Worksheets
        On Error Resume Next
        ws.Unprotect
        On Error GoTo 0

        Set rng = ws.UsedRange
        If Not rng Is Nothing Then
            With rng
                .Font.Name = "Yu Gothic"
            End With
        End If

        ' Áp d?ng font cho các shape (textbox, button...)
        For Each sh In ws.Shapes
            If sh.Type = msoTextBox Or sh.Type = msoShape Then
                On Error Resume Next
                sh.TextFrame2.TextRange.Font.Name = "Yu Mincho"
                On Error GoTo 0
            End If
        Next sh
    Next ws
End Sub


chạy build jar lamda:    mvn clean package -Plambda

kiểm tra port đang hoạt động:
netstat -ano | findstr :8080
đóng port đang hoạt động
taskkill /PID <PID> /F



Nếu có lỗi set default timezone khi chạy JVM
export MAVEN_OPTS="-Duser.timezone=Asia/Ho_Chi_Minh"
sau khi set sẽ không cần thêm đuôi nữa

# 1. Xem lịch sử migration
mvn flyway:info -Duser.timezone=UTC

# 2. Validate file migration  
mvn flyway:validate -Duser.timezone=UTC

# 3. Clean database (cẩn thận!)
mvn flyway:clean -Duser.timezone=UTC -Dflyway.cleanDisabled=false

# 4. Migrate (tạo/update bảng)
mvn flyway:migrate -Duser.timezone=UTC


