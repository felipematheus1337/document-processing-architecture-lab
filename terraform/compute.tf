data "aws_ami" "amazon_linux_2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name = "name"
    values = [
      "al2023-ami-*-x86_64"
    ]
  }

  filter {
    name = "architecture"
    values = [
      "x86_64"
    ]
  }

  filter {
    name = "virtualization-type"
    values = [
      "hvm"
    ]
  }
}

resource "aws_instance" "bastion" {
  ami                         = data.aws_ami.amazon_linux_2023.id
  instance_type               = var.bastion_instance_type
  subnet_id                   = aws_subnet.public[0].id
  vpc_security_group_ids      = [aws_security_group.ssh.id]
  key_name                    = var.key_pair_name
  associate_public_ip_address = true

  tags = {
    Name    = "${var.project_name}-bastion"
    Project = var.project_name
    Role    = "bastion"
  }
}

resource "aws_instance" "document_service" {
  ami                    = data.aws_ami.amazon_linux_2023.id
  instance_type          = var.ec2_instance_type
  subnet_id              = aws_subnet.private_app[0].id
  vpc_security_group_ids = [aws_security_group.app.id]
  key_name               = var.key_pair_name

  tags = {
    Name    = "${var.project_name}-document-service"
    Project = var.project_name
    Role    = "document-service"
  }
}

resource "aws_instance" "processing_service" {
  ami                    = data.aws_ami.amazon_linux_2023.id
  instance_type          = var.ec2_instance_type
  subnet_id              = aws_subnet.private_app[0].id
  vpc_security_group_ids = [aws_security_group.app.id]
  key_name               = var.key_pair_name

  tags = {
    Name    = "${var.project_name}-processing-service"
    Project = var.project_name
    Role    = "processing-service"
  }
}

resource "aws_instance" "export_job_service" {
  ami                    = data.aws_ami.amazon_linux_2023.id
  instance_type          = var.ec2_instance_type
  subnet_id              = aws_subnet.private_app[1].id
  vpc_security_group_ids = [aws_security_group.app.id]
  key_name               = var.key_pair_name

  tags = {
    Name    = "${var.project_name}-export-job-service"
    Project = var.project_name
    Role    = "export-job-service"
  }
}

resource "aws_instance" "kafka" {
  ami                    = data.aws_ami.amazon_linux_2023.id
  instance_type          = var.kafka_instance_type
  subnet_id              = aws_subnet.private_app[1].id
  vpc_security_group_ids = [aws_security_group.kafka.id]
  key_name               = var.key_pair_name

  tags = {
    Name    = "${var.project_name}-kafka"
    Project = var.project_name
    Role    = "kafka"
  }
}