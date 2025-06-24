
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Database, ArrowRight, TreePine, Search, Plus } from "lucide-react";
import { Link } from "react-router-dom";

const Index = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
      <div className="container mx-auto px-4 py-16">
        <div className="text-center mb-12">
          <div className="flex items-center justify-center mb-6">
            <Database className="h-16 w-16 text-blue-600" />
          </div>
          <h1 className="text-4xl font-bold text-slate-800 mb-4">
            Zookeeper Browser
          </h1>
          <p className="text-xl text-slate-600 max-w-2xl mx-auto">
            A modern, intuitive interface for browsing and managing your Apache Zookeeper ensemble. 
            Navigate your ZNode hierarchy with ease.
          </p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
          <Card className="border-blue-200 hover:shadow-lg transition-shadow">
            <CardHeader>
              <TreePine className="h-8 w-8 text-blue-600 mb-2" />
              <CardTitle>Tree Navigation</CardTitle>
              <CardDescription>
                Browse your ZNode hierarchy with an intuitive tree structure
              </CardDescription>
            </CardHeader>
          </Card>

          <Card className="border-green-200 hover:shadow-lg transition-shadow">
            <CardHeader>
              <Search className="h-8 w-8 text-green-600 mb-2" />
              <CardTitle>Node Details</CardTitle>
              <CardDescription>
                View comprehensive information about each ZNode including data and statistics
              </CardDescription>
            </CardHeader>
          </Card>

          <Card className="border-purple-200 hover:shadow-lg transition-shadow">
            <CardHeader>
              <Plus className="h-8 w-8 text-purple-600 mb-2" />
              <CardTitle>Node Management</CardTitle>
              <CardDescription>
                Create, edit, and delete ZNodes directly from the browser interface
              </CardDescription>
            </CardHeader>
          </Card>
        </div>

        <div className="text-center">
          <Card className="max-w-md mx-auto border-slate-200 bg-white/80 backdrop-blur-sm">
            <CardHeader>
              <CardTitle className="flex items-center justify-center gap-2">
                <Database className="h-5 w-5" />
                Get Started
              </CardTitle>
              <CardDescription>
                Connect to your Zookeeper instance at localhost:12345
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Link to="/zookeeper">
                <Button size="lg" className="w-full bg-blue-600 hover:bg-blue-700">
                  Open Zookeeper Browser
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Button>
              </Link>
            </CardContent>
          </Card>
        </div>

        <div className="mt-16 text-center">
          <div className="bg-slate-100 rounded-lg p-6 max-w-2xl mx-auto">
            <h3 className="text-lg font-semibold text-slate-800 mb-2">
              API Requirements
            </h3>
            <p className="text-slate-600 text-sm">
              This browser expects a Zookeeper HTTP API running on localhost:12345 with endpoints 
              for /get, /ls, /create, /set, and /delete operations.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Index;
