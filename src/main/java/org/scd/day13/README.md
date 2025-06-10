# Day 13

# 学习加载自定义嵌入模型 Qwen

## 使用服务的形式

### 安装依赖包
```bash
pip install flask==3.1.1 sentence-transformers==4.1.0
```

### 启动服务，加载本地模型
```python
from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
import logging

logging.basicConfig(level=logging.DEBUG)

app = Flask(__name__)

# Load the model
model = SentenceTransformer(model_name_or_path="D:\modelscope\models\Qwen\Qwen3-Embedding-0.6B")

@app.route('/embed', methods=['POST'])
def get_embedding():
    text = request.json['text']
    document_embeddings = model.encode(text)
    arr_list = document_embeddings.tolist()
    return jsonify({"embedding": arr_list})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
```

## 使用onnx

### 安装依赖
```shell
pip install optimum[exporters]
```

### 导出模型文件
```shell
optimum-cli export onnx --model D:\modelscope\models\Qwen\Qwen3-Embedding-0.6B --task feature-extraction --device cpu qwen_onnx_output/
```